package com.rotiking.client.common.auth;

import android.content.Context;
import android.os.Build;

import com.android.volley.Request;
import com.rotiking.client.common.settings.ApiKey;
import com.google.firebase.auth.FirebaseAuth;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Auth {
    public static String AUTH_TOKEN = "";
    public static String LOGIN_TOKEN = "";
    public static String ENCRYPTION_KEY = "";

    public static boolean isUserAuthenticated(Context context) {
        AuthPreferences preferences = new AuthPreferences(context);
        AUTH_TOKEN = preferences.getAuthToken();
        LOGIN_TOKEN = preferences.getLoginToken();
        ENCRYPTION_KEY = preferences.getEncryptionKey();
        return AUTH_TOKEN != null && LOGIN_TOKEN != null && FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getAuthUserUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public static void setAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        FirebaseAuth.getInstance().addAuthStateListener(listener);
    }

    public static class Signup {
        public static void signup(Context context, String name, String email, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);

            JSONObject body = new JSONObject();
            try {
                body.put("name", name);
                body.put("email", email);
            } catch (JSONException e) {
                promise.reject("Unable to Signup.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/signup/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }

        public static void signupOtpVerification(Context context, String token, String otp, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("SOT", token);

            JSONObject body = new JSONObject();
            try {
                body.put("otp", otp);
            } catch (JSONException e) {
                promise.reject("Unable to Verify.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/signup-otp/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }

        public static void signupPasswordCreation(Context context, String token, String password, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("SPT", token);

            JSONObject body = new JSONObject();
            try {
                body.put("password", password);
                body.put("package", context.getApplicationContext().getPackageName());
            } catch (JSONException e) {
                promise.reject("Unable to signup.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/signup-password/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }
    }

    public static class Login {
        public static void login(Context context, String email, String password, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);

            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
                body.put("password", password);
                body.put("device", Build.MODEL);
                body.put("package", context.getApplicationContext().getPackageName());
            } catch (JSONException e) {
                promise.reject("unable to Login.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/login/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }
    }

    public static class Recovery {
        public static void recoverAccount(Context context, String email, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);

            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
            } catch (JSONException e) {
                promise.reject("Unable to Signup.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/recover-password/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }

        public static void recoverAccountOtpVerification(Context context, String token, String otp, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("RAOT", token);

            JSONObject body = new JSONObject();
            try {
                body.put("otp", otp);
            } catch (JSONException e) {
                promise.reject("Unable to Verify.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/recover-password-otp/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }

        public static void recoveryPasswordCreation(Context context, String token, String password, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("RAPT", token);

            JSONObject body = new JSONObject();
            try {
                body.put("password", password);
            } catch (JSONException e) {
                promise.reject("Unable to signup.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/recover-new-password/", headers, body, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }
    }

    public static class Account {
        public static void profile(Context context, Promise<JSONObject> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("AT", Auth.AUTH_TOKEN);
            headers.put("LT", Auth.LOGIN_TOKEN);

            Server.request(context, Request.Method.GET, ApiKey.REQUEST_API_URL + "account/profile/", headers, null, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            promise.resolved(data);
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }

        public static void setProfilePhoto(Context context, String photo, Promise<String> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("AT", Auth.AUTH_TOKEN);
            headers.put("LT", Auth.LOGIN_TOKEN);

            JSONObject profile = new JSONObject();
            try {
                profile.put("photo", photo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/profile-photo/", headers, profile, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            try {
                                promise.resolved(data.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                promise.reject("Something went wrong.");
                            }
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }
    }
}
