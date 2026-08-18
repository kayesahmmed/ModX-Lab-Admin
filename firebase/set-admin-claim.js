/**
 * Run this ONCE, from your own computer, to mark the dedicated admin
 * Firebase Auth account as an admin. This script is NEVER part of the
 * Android app and the service-account key file it needs must never be
 * committed to git or placed inside the APK.
 *
 * Setup:
 *   1. Firebase Console -> Project settings -> Service accounts
 *      -> "Generate new private key" -> save as serviceAccountKey.json
 *      next to this script (keep it OUTSIDE any app / git repo).
 *   2. Firebase Console -> Authentication -> Add user -> create the
 *      admin@your-domain-or-project.app account used in AdminApi.java.
 *   3. npm install firebase-admin
 *   4. node set-admin-claim.js <the-user-uid-from-step-2>
 */

const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const uid = process.argv[2];
if (!uid) {
  console.error("Usage: node set-admin-claim.js <uid>");
  process.exit(1);
}

admin
  .auth()
  .setCustomUserClaims(uid, { admin: true })
  .then(() => {
    console.log(`Done. UID ${uid} now has admin: true.`);
    console.log("The app must sign out/in once (or call getIdToken(true)) to pick it up.");
    process.exit(0);
  })
  .catch((err) => {
    console.error(err);
    process.exit(1);
  });
