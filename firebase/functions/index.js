const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.database();

setGlobalOptions({ region: "asia-southeast1", maxInstances: 10, enforceAppCheck: true });

function requireAdmin(request) {
  if (!request.auth || request.auth.token.admin !== true) {
    throw new HttpsError("permission-denied", "Only an authorized admin account may call this function.");
  }
}

function requireAuth(request) {
  if (!request.auth) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }
}

function requireNonEmptyString(value, field) {
  if (typeof value !== "string" || value.trim() === "") {
    throw new HttpsError("invalid-argument", `"${field}" is required.`);
  }
  return value.trim();
}

function nowStamp(date) {
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(date.getHours())}:${pad(date.getMinutes())} - ${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
}

// 1. Create Reseller
exports.createReseller = onCall(async (request) => {
  requireAdmin(request);
  const username = requireNonEmptyString(request.data.username, "username");
  const password = requireNonEmptyString(request.data.password, "password");
  const access = requireNonEmptyString(request.data.access, "access"); // "1" or "unlimited"
  const coinStr = requireNonEmptyString(String(request.data.coin ?? "0"), "coin");

  const credits = parseFloat(coinStr.replace("$", ""));
  if (isNaN(credits) || credits < 0) {
    throw new HttpsError("invalid-argument", "Invalid coin amount.");
  }

  const device_restriction = access === "1" ? "single" : "unlimited";

  // Check username duplicate
  const indexRef = db.ref(`UsernameIndex/${username}`);
  const snap = await indexRef.get();
  if (snap.exists()) {
    throw new HttpsError("already-exists", "Username already exists.");
  }

  const email = `${username}@reseller.modx.local`;

  let userRecord;
  try {
    userRecord = await admin.auth().createUser({
      email,
      password,
      displayName: username,
    });
  } catch (error) {
    throw new HttpsError("internal", `Failed to create auth user: ${error.message}`);
  }

  const uid = userRecord.uid;
  const now = new Date();

  // Transaction to secure index and data
  await indexRef.set(uid);

  await db.ref(`Resellers/${uid}`).set({
    username,
    credits,
    device_restriction,
    bound_hwid: "",
    status: "active",
    role: "reseller",
    created_at: now.getTime(),
  });

  return { uid, username };
});

// 2. Validate Reseller Device
exports.validateResellerDevice = onCall(async (request) => {
  requireAuth(request);
  const uid = request.auth.uid;
  const hwid = requireNonEmptyString(request.data.hwid, "hwid");

  const resellerRef = db.ref(`Resellers/${uid}`);
  const snap = await resellerRef.get();
  if (!snap.exists()) throw new HttpsError("not-found", "Reseller not found.");

  const reseller = snap.val();
  if (reseller.status !== "active") throw new HttpsError("permission-denied", "Reseller is not active.");

  if (reseller.device_restriction === "single") {
    // Atomic binding
    const { committed, snapshot } = await resellerRef.child("bound_hwid").transaction((currentHwid) => {
      if (currentHwid === null || currentHwid === "") {
        return hwid; // bind
      }
      return undefined; // abort transaction
    });
    
    // If not committed, it means bound_hwid was already set
    if (!committed) {
       const boundHwid = snapshot.val();
       if (boundHwid !== hwid) {
           throw new HttpsError("permission-denied", "Device Access Restricted.");
       }
    }
  }

  return { valid: true };
});

// 3. Generate Key
exports.generateKey = onCall(async (request) => {
  requireAuth(request);
  const uid = request.auth.uid;
  const hwid = requireNonEmptyString(request.data.hwid, "hwid");
  const username = requireNonEmptyString(request.data.username, "username");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const validityHours = parseFloat(request.data.validityHours);
  const access = requireNonEmptyString(request.data.access, "access");
  
  if (isNaN(validityHours) || validityHours <= 0) {
     throw new HttpsError("invalid-argument", "validityHours must be positive.");
  }

  // Cost calculation (server-side, don't trust client)
  // For example: $25 for 30 days (720 hrs), so 720 / 720 * 25
  const cost = (validityHours / 720) * 25; 

  const resellerRef = db.ref(`Resellers/${uid}`);
  
  // Transaction
  const result = await resellerRef.child("credits").transaction((currentCredits) => {
    if (currentCredits === null) return undefined;
    if (currentCredits >= cost) {
      return currentCredits - cost;
    }
    return undefined; // insufficient
  });

  if (!result.committed) {
    throw new HttpsError("permission-denied", "Insufficient Credits or error.");
  }

  const now = Date.now();
  const expiryMillis = now + (validityHours * 3600_000);
  
  // Create Key
  const generatedKey = "-NoxUser" + Math.random().toString(36).substring(2, 10).toUpperCase();
  
  const keyData = {
    user: username,
    pass: pass,
    status: "true",
    access: access,
    key: generatedKey,
    device: "null",
    version: "null",
    rgtime: nowStamp(new Date(now)),
    time: String(expiryMillis),
    Validity: nowStamp(new Date(expiryMillis)),
    generated_by: uid
  };

  await db.ref(`Keys/${generatedKey}`).set(keyData);
  
  // Also keep old 'User' node working for compatibility
  await db.ref(`User/${generatedKey}`).set(keyData);

  // Audit Logs
  await db.ref(`AuditLogs`).push({
      reseller_uid: uid,
      action: "generate_key",
      cost,
      key: generatedKey,
      timestamp: now
  });

  return { key: generatedKey };
});

// Other existing admin functions for compatibility
exports.addUser = onCall(async (request) => {
  requireAdmin(request);
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const access = requireNonEmptyString(request.data.access, "access");
  const validityHours = parseFloat(request.data.validityHours);
  
  if (isNaN(validityHours) || validityHours <= 0) {
    throw new HttpsError("invalid-argument", "validityHours must be a positive number.");
  }

  const now = new Date();
  const expiry = new Date(now.getTime() + validityHours * 3600_000);
  const ref = db.ref("User").push();
  const key = ref.key;

  const data = {
    user,
    pass,
    status: "true",
    access,
    key,
    device: "null",
    version: "null",
    rgtime: nowStamp(now),
    time: String(expiry.getTime()),
    Validity: nowStamp(expiry),
  };

  await ref.set(data);
  await db.ref(`Keys/${key}`).set(data);

  return { key };
});

exports.setMaintenanceUpdate = onCall(async (request) => {
  requireAdmin(request);
  const version = requireNonEmptyString(request.data.version, "version");
  const message = requireNonEmptyString(request.data.message, "message");
  const link = requireNonEmptyString(request.data.link, "link");
  await db.ref("update/up").update({ version, message, link });
  return { ok: true };
});


exports.updateUserCredentials = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const snap = await db.ref(`User/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "User key not found.");
  await db.ref(`User/${key}`).update({ user, pass });
  await db.ref(`Keys/${key}`).update({ user, pass });
  return { ok: true };
});

exports.toggleUserStatus = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const status = request.data.status === "true" ? "true" : "false";
  const snap = await db.ref(`User/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "User key not found.");
  await db.ref(`User/${key}`).update({ status });
  await db.ref(`Keys/${key}`).update({ status });
  return { ok: true };
});

exports.deleteUser = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  await db.ref(`User/${key}`).remove();
  await db.ref(`Keys/${key}`).remove();
  return { ok: true };
});

exports.updateSellerCredentials = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const snap = await db.ref(`Seller/${key}`).get();
  if (snap.exists()) {
      await db.ref(`Seller/${key}`).update({ user, pass });
  }
  // also check resellers
  const rsnap = await db.ref(`Resellers/${key}`).get();
  if (rsnap.exists()) {
      // update firebase auth password
      try {
        await admin.auth().updateUser(key, { password: pass });
      } catch(e) {}
  }
  return { ok: true };
});

exports.toggleSellerStatus = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const status = request.data.status === "true" ? "true" : "false";
  const statusStr = request.data.status === "true" ? "active" : "inactive";
  const snap = await db.ref(`Seller/${key}`).get();
  if (snap.exists()) {
     await db.ref(`Seller/${key}`).update({ status });
  }
  const rsnap = await db.ref(`Resellers/${key}`).get();
  if (rsnap.exists()) {
     await db.ref(`Resellers/${key}`).update({ status: statusStr });
  }
  return { ok: true };
});

exports.deleteSeller = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  await db.ref(`Seller/${key}`).remove();
  // also check resellers
  const rsnap = await db.ref(`Resellers/${key}`).get();
  if (rsnap.exists()) {
     const data = rsnap.val();
     await db.ref(`Resellers/${key}`).remove();
     await db.ref(`UsernameIndex/${data.username}`).remove();
     try {
       await admin.auth().deleteUser(key);
     } catch(e) {}
  }
  return { ok: true };
});
