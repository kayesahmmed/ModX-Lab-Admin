# ModxLab Admin — Firebase Security Hardening

## ১. স্থাপত্য (Architecture) — সংক্ষেপে

আগে যা হতো: Admin App সরাসরি `User` ও `Seller` নোডে `updateChildren()` / `removeValue()` কল করত। মানে Firebase Rules যদি `.write: true` না রাখেন, অ্যাপ নিজেই লিখতে পারত না — তাই ডেভেলপাররা প্রায়ই বাধ্য হয়ে `.write: true` রেখে দেন, যেটা যেকোনো ব্যক্তি APK ডিকম্পাইল করে API key বের করে পুরো ডেটাবেস মুছে/বদলে দিতে পারে।

এখন যেভাবে কাজ করবে:

```
Admin App (Sketchware)
   │  Firebase Auth দিয়ে sign-in (শুধু read-permission পায়)
   │  Cloud Function কল করে (addUser, deleteUser, ...)
   ▼
Cloud Function (Google-এর সার্ভারে চলে, Admin SDK)
   │  caller-এর admin custom-claim যাচাই করে
   │  ইনপুট validate করে
   ▼
Firebase Realtime Database (User / Seller / update)
```

- APK-এর ভিতরে **কোনো Firebase Admin SDK / service-account key নেই**।
- Realtime Database Rules-এ **সব জায়গায় `.write: false`** — অ্যাপ (এমনকি admin অ্যাকাউন্ট দিয়ে সাইন-ইন থাকলেও) সরাসরি লিখতে পারবে না। লেখা কেবল Cloud Function-এর মাধ্যমেই সম্ভব, আর Cloud Function নিজে থেকেও admin claim যাচাই করে (তাই rules বাইপাস হলেও ফাংশন আটকাবে — defense in depth)।
- `User` ও `Seller` নোড পড়তেও (`.read`) সাইন-ইন করা admin অ্যাকাউন্ট লাগবে, যার token-এ `admin: true` claim আছে।

---

## ২. Activity ভিত্তিক পরিবর্তনের তালিকা (আপনার প্রশ্ন ১ ও ২)

| Activity | কোথায় (Event/Block) | আগে | এখন |
|---|---|---|---|
| **AddUserActivity** | `button1` → `onClick` | `key = User.push().getKey(); ... User.child(key).updateChildren(map);` | `AdminApi.addUser(...)` কল — key generation ও লেখা এখন Cloud Function-এর ভেতরে হয় |
| **EditUserActivity** | `materialbutton1` → `onClick` | `User.child(key).updateChildren(map)` (user/pass আপডেট) | `AdminApi.updateUserCredentials(...)` |
| **EditUserActivity** | `t_b` (Activate/Deactivate বাটন) → `onClick` | `User.child(key).updateChildren(map1)` (status টগল) | `AdminApi.toggleUserStatus(...)` |
| **UserListFragmentActivity** | Adapter-এর `delete` বাটন → `onClick` (Delete dialog) | `User.child(...).removeValue()` | `AdminApi.deleteUser(...)` |
| **UserListFragmentActivity** | `linear1` → `onLongClick` (Activate/Deactivate dialog) | `User.child(...).updateChildren(map)` | `AdminApi.toggleUserStatus(...)` |
| **AddSellerActivity** | `button1` → `onClick` | `Seller.child(key).updateChildren(map)` | `AdminApi.addSeller(...)` |
| **EditSellerActivity** | `materialbutton1` → `onClick` | `Seller.child(key).updateChildren(map)` | `AdminApi.updateSellerCredentials(...)` |
| **EditSellerActivity** | `t_b` → `onClick` | `Seller.child(key).updateChildren(map1)` | `AdminApi.toggleSellerStatus(...)` |
| **SellerListFragmentActivity** | `delete` বাটন → `onClick` | `Seller.child(...).removeValue()` | `AdminApi.deleteSeller(...)` |
| **SellerListFragmentActivity** | `linear1` → `onLongClick` | `Seller.child(...).updateChildren(map)` | `AdminApi.toggleSellerStatus(...)` |
| **MaintenanceActivity** | `materialbutton1` → `onClick` | `update.child("up").updateChildren(map)` | `AdminApi.setMaintenanceUpdate(...)` |
| **MainActivity** | `onCreate` (Activity event) | — | `AdminApi.ensureSignedIn(null);` যোগ হয়েছে — অ্যাপ চালু হওয়ার সাথে সাথেই নিরবে admin অ্যাকাউন্টে sign-in করে, কোনো নতুন UI/স্ক্রিন নেই |
| প্রতিটি write-touching Activity/Fragment | `onCreate` / `FirebaseApp.initializeApp(...)` এর ঠিক পরে | — | `AdminApi.ensureSignedIn(null);` — নিরাপত্তা backup, যদি MainActivity-এর sign-in তখনো শেষ না হয়ে থাকে |

UI/লে-আউট, বাটনের নাম, ডায়ালগ, টোস্ট মেসেজ — সব **অপরিবর্তিত** রাখা হয়েছে। শুধু বাটনের ভেতরের Firebase-কল বদলানো হয়েছে।

---

## ৩. সম্পূর্ণ Java কোড — কোথায় পাবেন

`android/` ফোল্ডারে প্রতিটি Activity-এর সম্পূর্ণ, প্রস্তুত ফাইল আছে — Sketchware-এর "Java Compile" মোডে গিয়ে পুরনো কোড মুছে এই ফাইলের পুরো কনটেন্ট পেস্ট করে দিন:

- `AddUserActivity.java`, `EditUserActivity.java`, `UserListFragmentActivity.java`
- `AddSellerActivity.java`, `EditSellerActivity.java`, `SellerListFragmentActivity.java`
- `MaintenanceActivity.java`, `MainActivity.java`

**নতুন একটা ফাইল যোগ করতে হবে:** `AdminApi.java` — এটা কোনো Activity না, একটা সাধারণ helper ক্লাস। আপনার প্রজেক্টে `SketchwareUtil.java` / `FileUtil.java` যেভাবে যোগ করা আছে (স্ক্রিনশট ৩-এ দেখা গেছে), ঠিক একই জায়গা থেকে "Add new Java file/class" করে নাম `AdminApi.java` দিয়ে এই কোড পেস্ট করুন।

**build.gradle-এ একটা লাইন যোগ করতে হবে** (`android/build.gradle` ফাইলে দেখানো আছে):
```gradle
implementation 'com.google.firebase:firebase-functions'
```
(`firebase-auth` আগে থেকেই আপনার build.gradle-এ ছিল, তাই সেটা বাদ দেওয়া লাগেনি।)

`AdminApi.java`-তে দুটো জায়গা নিজের মতো বসাতে হবে:
```java
private static final String ADMIN_EMAIL = "admin@your-domain-or-project.app";
private static final String ADMIN_PASSWORD = "REPLACE_WITH_A_STRONG_PASSWORD";
```
এই ইমেইল/পাসওয়ার্ড দিয়ে Firebase Console → Authentication → Add user করে একটা **একটামাত্র, শুধু এই Admin App-এর জন্য নিবেদিত** অ্যাকাউন্ট বানাবেন। ব্যক্তিগত Google অ্যাকাউন্ট বা অন্য কোনো কাজে ব্যবহৃত অ্যাকাউন্ট এখানে দেবেন না।

---

## ৪. Firebase Rules ও Cloud Function (আপনার প্রশ্ন ৪)

`firebase/database.rules.json`:
```json
{
  "rules": {
    ".read": false,
    ".write": false,
    "User":   { ".read": "auth != null && auth.token.admin === true", ".write": false },
    "Seller": { ".read": "auth != null && auth.token.admin === true", ".write": false },
    "update": { ".read": true, ".write": false }
  }
}
```
- `.write` সব জায়গায় সবসময় `false` — কেউই সরাসরি লিখতে পারবে না, শুধু Cloud Function পারবে (Admin SDK ব্যবহার করে, যা rules-কে পুরোপুরি বাইপাস করে, কারণ Admin SDK সার্ভার-সাইড এবং rules Admin SDK-এর ওপর প্রযোজ্য না)।
- `User` ও `Seller` পড়তে signed-in admin claim লাগবে।
- **`update` নোড ইচ্ছাকৃতভাবে পাবলিকলি readable রাখা হয়েছে** — কারণ সাধারণত এই ধরনের key-system-এ `/update` নোডটাই আপনার **মূল (licensed) অ্যাপ** ফোর্স-আপডেট/মেইনটেন্যান্স মেসেজ চেক করতে পড়ে, যেটা এই zip-এ নেই। যদি আপনার মূল অ্যাপও `/User` সরাসরি পড়ে কী (key) ভ্যালিডেট করে, সেটা এখনই ভেঙে যাবে — কারণ এখন `/User`-ও admin-only। এই ক্ষেত্রে নিচের `validateLicenseKey` ফাংশনটা ব্যবহার করুন (এটা `index.js`-এ বোনাস হিসেবে দেওয়া আছে) — মূল অ্যাপ থেকে সরাসরি DB read না করে এই ফাংশন কল করবে, তাহলে `/User`-ও পুরোপুরি `.read: false` করে দেওয়া যাবে।

`firebase/functions/index.js`-এ ৯টা Cloud Function আছে: `addUser`, `updateUserCredentials`, `toggleUserStatus`, `deleteUser`, `addSeller`, `updateSellerCredentials`, `toggleSellerStatus`, `deleteSeller`, `setMaintenanceUpdate` — প্রতিটা প্রথমেই `requireAdmin()` দিয়ে caller-এর admin claim চেক করে, তারপর ইনপুট validate করে, তারপর Admin SDK দিয়ে লেখে। প্লাস একটা বোনাস পাবলিক ফাংশন `validateLicenseKey` (উপরে ব্যাখ্যা করা হয়েছে)।

### Deploy করার ধাপ (নিজের কম্পিউটার/Cloud Shell থেকে, APK-এর সাথে সম্পর্কহীন)
```bash
npm install -g firebase-tools
firebase login
firebase init functions        # প্রজেক্ট: kayes-ahmmed-pro বেছে নিন, ভাষা: JavaScript
# firebase/functions/index.js ও package.json জেনারেট হওয়া functions/ ফোল্ডারে কপি করুন
firebase deploy --only functions,database
```

### Admin claim সেট করা (একবারই করতে হয়)
1. Firebase Console → Authentication → Add user → উপরের `ADMIN_EMAIL`/`ADMIN_PASSWORD` দিয়ে অ্যাকাউন্ট বানান, UID কপি করুন।
2. Firebase Console → Project settings → Service accounts → Generate new private key → `serviceAccountKey.json` হিসেবে সেভ করুন (এটা APK বা git-এ কখনো দেবেন না, শুধু নিজের কম্পিউটারে রাখুন)।
3. `firebase/set-admin-claim.js` থাকা ফোল্ডারে গিয়ে:
   ```bash
   npm install firebase-admin
   node set-admin-claim.js <UID>
   ```

---

## সততার সাথে কিছু সীমাবদ্ধতা (Trade-offs)

- **AdminApi.java-তে থাকা email/password এখনও APK-এর ভিতরে থাকে**, চাইলে ডিকম্পাইল করে বের করা সম্ভব। কিন্তু আগের অবস্থার (open `.write: true`) তুলনায় এটা অনেক শক্তিশালী, কারণ: (ক) এই অ্যাকাউন্ট দিয়ে **শুধু read** করা যায়, কোনো write না — write সব সময় Cloud Function-এর ভেতরের নিজস্ব admin-claim চেকের ওপর নির্ভর করে; (খ) কেউ এই ক্রেডেনশিয়াল বের করলেও আপনি এক ক্লিকে Firebase Console থেকে ওই একটা অ্যাকাউন্ট disable/reset করে দিতে পারবেন — নতুন APK release লাগবে না।
- আরও শক্ত করতে চাইলে **Firebase App Check** (Play Integrity provider) চালু করুন — এটা নিশ্চিত করে যে Cloud Function/Database কল শুধু আপনার আসল, আনমডিফাইড APK থেকেই আসছে, ইমুলেটর বা টেম্পার করা ক্লায়েন্ট থেকে না। এটা APK-এর ভিতরের ক্রেডেনশিয়াল বের করে অপব্যবহারের ঝুঁকি অনেকটাই কমায়। এই বিষয়ে সাহায্য দরকার হলে বলবেন, আলাদাভাবে যোগ করে দিতে পারি।
- `add_seller`/`edit_seller`-এ `coin` ফিল্ডের টাইপ EditText থেকে string আসছিল, তাই Cloud Function-এও সেটা string হিসেবে রাখা হয়েছে (আচরণ অপরিবর্তিত)।
