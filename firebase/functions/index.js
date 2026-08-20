/**
 * Cloud Functions for the ModxLab Admin app.
 * -----------------------------------------------------------------------
 * Deploy this with the Firebase CLI, from your own computer:
 *
 *   npm install -g firebase-tools
 *   firebase login
 *   firebase init functions      (choose the "kayes-ahmmed-pro" project, JavaScript)
 *   # copy this file + package.json into the generated "functions" folder
 *   firebase deploy --only functions,database
 *
 * The service-account credentials used by admin.initializeApp() below are
 * provided AUTOMATICALLY by Google Cloud when the function runs on Google's
 * servers. You never download, copy, or embed any key file -- especially
 * never inside the Android app.
 */

const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.database();

// Keep every function in one region close to your Realtime Database.
setGlobalOptions({ region: "asia-southeast1", maxInstances: 10 });

/** Throws if the caller is not signed in with the "admin" custom claim. */
function requireAdmin(request) {
  if (!request.auth || request.auth.token.admin !== true) {
    throw new HttpsError(
      "permission-denied",
      "Only an authorized admin account may call this function."
    );
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
  return `${pad(date.getHours())}:${pad(date.getMinutes())} - ${pad(
    date.getDate()
  )}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
}

// ============================================================= User =====

exports.addUser = onCall(async (request) => {
  requireAdmin(request);
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const access = requireNonEmptyString(request.data.access, "access");
  const validityDaysRaw = request.data.validityDays;
  const validityDays = Number(validityDaysRaw);
  if (!Number.isFinite(validityDays) || validityDays <= 0) {
    throw new HttpsError("invalid-argument", "validityDays must be a positive number.");
  }

  const ref = db.ref("User").push();
  const key = ref.key;

  const now = new Date();
  const expiry = new Date(now.getTime() + validityDays * 24 * 60 * 60 * 1000);

  await ref.set({
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
  });

  return { key };
});

exports.updateUserCredentials = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");

  const snap = await db.ref(`User/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "User key not found.");

  await db.ref(`User/${key}`).update({ user, pass });
  return { ok: true };
});

exports.toggleUserStatus = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const status = request.data.status === "true" ? "true" : "false";

  const snap = await db.ref(`User/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "User key not found.");

  await db.ref(`User/${key}`).update({ status });
  return { ok: true };
});

exports.deleteUser = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  await db.ref(`User/${key}`).remove();
  return { ok: true };
});

// =========================================================== Seller =====

exports.addSeller = onCall(async (request) => {
  requireAdmin(request);
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");
  const access = requireNonEmptyString(request.data.access, "access");
  const coin = requireNonEmptyString(String(request.data.coin ?? ""), "coin");

  const ref = db.ref("Seller").push();
  const key = ref.key;

  await ref.set({
    user,
    pass,
    status: "true",
    access,
    key,
    device: "null",
    version: "null",
    coin,
  });

  return { key };
});

exports.updateSellerCredentials = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const user = requireNonEmptyString(request.data.user, "user");
  const pass = requireNonEmptyString(request.data.pass, "pass");

  const snap = await db.ref(`Seller/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "Seller key not found.");

  await db.ref(`Seller/${key}`).update({ user, pass });
  return { ok: true };
});

exports.toggleSellerStatus = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  const status = request.data.status === "true" ? "true" : "false";

  const snap = await db.ref(`Seller/${key}`).get();
  if (!snap.exists()) throw new HttpsError("not-found", "Seller key not found.");

  await db.ref(`Seller/${key}`).update({ status });
  return { ok: true };
});

exports.deleteSeller = onCall(async (request) => {
  requireAdmin(request);
  const key = requireNonEmptyString(request.data.key, "key");
  await db.ref(`Seller/${key}`).remove();
  return { ok: true };
});

// ====================================================== Maintenance =====

exports.setMaintenanceUpdate = onCall(async (request) => {
  requireAdmin(request);
  const version = requireNonEmptyString(request.data.version, "version");
  const message = requireNonEmptyString(request.data.message, "message");
  const link = requireNonEmptyString(request.data.link, "link");

  await db.ref("update/up").update({ version, message, link });
  return { ok: true };
});

// ============================================ Optional: client app use =====
// If your MAIN (licensed) app currently reads /User directly to validate a
// key, replace that direct read with this public, read-only function instead
// -- so you can set "User" to fully ".read": false in the rules too.
// It returns only the fields a client needs, never the full record list.

exports.validateLicenseKey = onCall(async (request) => {
  const key = requireNonEmptyString(request.data.key, "key");
  const snap = await db.ref(`User/${key}`).get();
  if (!snap.exists()) {
    return { valid: false, reason: "not_found" };
  }
  const val = snap.val();
  const isActive = val.status === "true";
  const isExpired = Number(val.time) < Date.now();
  return {
    valid: isActive && !isExpired,
    status: val.status,
    expiresAt: val.time,
  };
});
