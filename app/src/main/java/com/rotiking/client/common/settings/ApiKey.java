package com.rotiking.client.common.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.rotiking.client.common.security.AES128;
import com.rotiking.client.common.security.Preferences;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.utils.Promise;

import java.util.Base64;

public class ApiKey {
    static {
        System.loadLibrary("client");
    }

    public static String REQUEST_API_URL = "";
    public static String REQUEST_API_KEY = "";

    private native static String getFirebaseAccessServerKey();

    public static void loadServerApiKey(Context context, Promise<Object> promise) {
        promise.resolving(0, null);

        try {
            SharedPreferences preferences = Preferences.getEncryptedSharedPreferences(context, "APP_SERVER_API");
            promise.resolving(10, null);

            if (preferences.getString("REQUEST_API_KEY", null) == null) {
                String doc = new String(Base64.getDecoder().decode(getFirebaseAccessServerKey())).trim();
                promise.resolving(50, null);

                FirebaseFirestore.getInstance().collection("app").document(doc).get().addOnSuccessListener(documentSnapshot -> {
                    promise.resolving(75, null);

                    String encryptedKeyString = documentSnapshot.get("REQUEST_API_KEY", String.class);
                    ApiKey.REQUEST_API_KEY = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, encryptedKeyString);
                    ApiKey.REQUEST_API_URL = documentSnapshot.get("REQUEST_API_URL", String.class);

                    promise.resolving(95, null);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("REQUEST_API_KEY", ApiKey.REQUEST_API_KEY);
                    editor.putString("REQUEST_API_URL", ApiKey.REQUEST_API_URL);
                    editor.apply();

                    promise.resolving(100, null);
                    promise.resolved(null);

                }).addOnFailureListener(e -> promise.reject("Unable to connect to server."));

            } else {
                promise.resolving(50, null);

                ApiKey.REQUEST_API_KEY = preferences.getString("REQUEST_API_KEY", null);
                ApiKey.REQUEST_API_URL = preferences.getString("REQUEST_API_URL", null);
                promise.resolving(100, null);
                promise.resolved(null);
            }
        } catch (Exception e) {
            promise.reject("Unable to connect to server.");
        }
    }
}
