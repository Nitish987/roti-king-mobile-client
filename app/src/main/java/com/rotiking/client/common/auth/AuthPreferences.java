package com.rotiking.client.common.auth;

import com.rotiking.client.common.security.Preferences;
import com.rotiking.client.common.settings.TokenNames;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences {
    private SharedPreferences sharedPreferences;

    public AuthPreferences(Context context) {
        try {
            sharedPreferences = Preferences.getEncryptedSharedPreferences(context, "AUTH_TOKEN");
        } catch (Exception e) {
            e.printStackTrace();
            sharedPreferences = null;
        }
    }

    public void setAuthToken(String authToken, String loginToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TokenNames.AT, authToken);
        editor.putString(TokenNames.LT, loginToken);
        editor.apply();
    }

    public void setEncryptionKey(String encKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ENC_KEY", encKey);
        editor.apply();
    }

    public void setPaymentKey(String encKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PAY_KEY", encKey);
        editor.apply();
    }

    public String getAuthToken() {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString(TokenNames.AT, null);
    }

    public String getLoginToken() {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString(TokenNames.LT, null);
    }

    public String getEncryptionKey() {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString("ENC_KEY", null);
    }

    public String getPaymentKey() {
        if (sharedPreferences == null) return null;
        return sharedPreferences.getString("PAY_KEY", null);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
