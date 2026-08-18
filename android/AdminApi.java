package com.modxlab.admin;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminApi.java
 * -----------------------------------------------------------------------
 * Add this file to the project EXACTLY the way SketchwareUtil.java / FileUtil.java
 * were added (Custom Java Class / Local Library -> Add new file -> paste this code).
 * It does not touch any layout/UI. Package must stay "com.modxlab.admin" so every
 * Activity in the project can call it without an extra import.
 *
 * WHY THIS FILE EXISTS
 * Every place in the app that used to do:
 *      User.child(key).updateChildren(map);
 *      User.child(key).removeValue();
 * now calls a Cloud Function instead (see firebase/functions/index.js). The Cloud
 * Function runs on Google's servers with the Firebase Admin SDK -- that SDK / any
 * service-account key is NEVER shipped inside this APK. The phone only ever holds
 * a normal Firebase Auth session for one dedicated "admin" account, and the
 * Realtime Database rules (see firebase/database.rules.json) make sure that even
 * that account cannot write to the database directly -- .write is false for
 * everyone, always. Only the Cloud Function (running on Google's servers, using
 * the Admin SDK) can write, and it double-checks the caller's admin claim itself.
 */
public class AdminApi {

    // TODO: replace with the email/password of the ONE dedicated Firebase Auth
    // account you create for this admin app in Firebase Console -> Authentication.
    // Do NOT reuse a personal Google account. See README for step-by-step setup.
    private static final String ADMIN_EMAIL = "admin@your-domain-or-project.app";
    private static final String ADMIN_PASSWORD = "REPLACE_WITH_A_STRONG_PASSWORD";

    public interface Callback {
        void onSuccess(HashMap<String, Object> data);
        void onError(String message);
    }

    /**
     * Makes sure we have a signed-in Firebase Auth session before any Cloud
     * Function / rule-protected read is attempted. Safe to call many times --
     * it is a no-op if already signed in. Pass null for onReady if you don't
     * need to wait for it (e.g. calling it once at app start).
     */
    public static void ensureSignedIn(final Runnable onReady) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            if (onReady != null) onReady.run();
            return;
        }
        auth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Force-refresh so a freshly-set custom claim ("admin": true)
                        // is picked up immediately instead of waiting for the next hour.
                        if (task.isSuccessful() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true);
                        }
                        if (onReady != null) onReady.run();
                    }
                });
    }

    private static final FirebaseFunctions functions = FirebaseFunctions.getInstance();

    private static void call(final String name, final Map<String, Object> data, final Callback cb) {
        ensureSignedIn(new Runnable() {
            @Override
            public void run() {
                functions.getHttpsCallable(name)
                        .call(data)
                        .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                            @Override
                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> out = new HashMap<>();
                                    Object raw = task.getResult() != null ? task.getResult().getData() : null;
                                    if (raw instanceof Map) {
                                        //noinspection unchecked
                                        out.putAll((Map<String, Object>) raw);
                                    }
                                    if (cb != null) cb.onSuccess(out);
                                } else {
                                    String msg = task.getException() != null
                                            ? task.getException().getMessage()
                                            : "Unknown error";
                                    if (cb != null) cb.onError(msg);
                                }
                            }
                        });
            }
        });
    }

    // ---------------------------------------------------------------- User
    public static void addUser(String user, String pass, String access, String validityDays, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("pass", pass);
        data.put("access", access);
        data.put("validityDays", validityDays);
        call("addUser", data, cb);
    }

    public static void updateUserCredentials(String key, String user, String pass, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("user", user);
        data.put("pass", pass);
        call("updateUserCredentials", data, cb);
    }

    public static void toggleUserStatus(String key, boolean activate, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("status", activate ? "true" : "false");
        call("toggleUserStatus", data, cb);
    }

    public static void deleteUser(String key, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        call("deleteUser", data, cb);
    }

    // -------------------------------------------------------------- Seller
    public static void addSeller(String user, String pass, String access, String coin, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("pass", pass);
        data.put("access", access);
        data.put("coin", coin);
        call("addSeller", data, cb);
    }

    public static void updateSellerCredentials(String key, String user, String pass, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("user", user);
        data.put("pass", pass);
        call("updateSellerCredentials", data, cb);
    }

    public static void toggleSellerStatus(String key, boolean activate, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("status", activate ? "true" : "false");
        call("toggleSellerStatus", data, cb);
    }

    public static void deleteSeller(String key, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        call("deleteSeller", data, cb);
    }

    // --------------------------------------------------- Maintenance / update
    public static void setMaintenanceUpdate(String version, String message, String link, Callback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("message", message);
        data.put("link", link);
        call("setMaintenanceUpdate", data, cb);
    }
}
