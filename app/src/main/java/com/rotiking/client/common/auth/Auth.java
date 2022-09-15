package com.rotiking.client.common.auth;

import android.content.Context;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rotiking.client.common.settings.ApiKey;
import com.google.firebase.auth.FirebaseAuth;
import com.rotiking.client.utils.Promise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Auth {
    public static String AUTH_TOKEN = "";
    public static String LOGIN_TOKEN = "";

    public static boolean isUserAuthenticated(Context context) {
        AuthPreferences preferences = new AuthPreferences(context);
        AUTH_TOKEN = preferences.getAuthToken();
        LOGIN_TOKEN = preferences.getLoginToken();
        return AUTH_TOKEN != null && LOGIN_TOKEN != null && FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static class Signup {
        public static void signup(Context context, String name, String email, Promise promise) {
            promise.resolving(0, null);

            String url = ApiKey.REQUEST_API_URL + "account/signup/";
            RequestQueue queue = Volley.newRequestQueue(context);
            promise.resolving(33, null);

            JSONObject body = new JSONObject();
            try {
                body.put("name", name);
                body.put("email", email);
            } catch (JSONException e) {
                promise.reject("Unable to Signup.");
                e.printStackTrace();
                return;
            }

            promise.resolving(75, null);
            queue.add(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        promise.resolving(100, null);
                        promise.resolved(response);
                    },
                    error -> promise.reject("unable to Signup!")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("RAK", ApiKey.REQUEST_API_KEY);
                    return headers;
                }
            });
        }

        public static void signupOtpVerification(Context context, String token, String otp, Promise promise) {
            promise.resolving(0, null);

            String url = ApiKey.REQUEST_API_URL + "account/signup-otp/";
            RequestQueue queue = Volley.newRequestQueue(context);
            promise.resolving(33, null);

            JSONObject body = new JSONObject();
            try {
                body.put("otp", otp);
            } catch (JSONException e) {
                promise.reject("Unable to Verify.");
                e.printStackTrace();
                return;
            }

            promise.resolving(75, null);
            queue.add(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        promise.resolving(100, null);
                        promise.resolved(response);
                    },
                    error -> promise.reject("Unable to Verify.")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("RAK", ApiKey.REQUEST_API_KEY);
                    headers.put("SOT", token);
                    return headers;
                }
            });
        }

        public static void signupPasswordCreation(Context context, String token, String password, Promise promise) {
            promise.resolving(0, null);

            String url = ApiKey.REQUEST_API_URL + "account/signup-password/";
            RequestQueue queue = Volley.newRequestQueue(context);
            promise.resolving(33, null);

            JSONObject body = new JSONObject();
            try {
                body.put("password", password);
                body.put("package", context.getApplicationContext().getPackageName());
            } catch (JSONException e) {
                promise.reject("Unable to signup.");
                e.printStackTrace();
                return;
            }

            promise.resolving(75, null);
            queue.add(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        promise.resolving(100, null);
                        promise.resolved(response);
                    },
                    error -> promise.reject("Unable to signup.")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("RAK", ApiKey.REQUEST_API_KEY);
                    headers.put("SPT", token);
                    return headers;
                }
            });
        }
    }

    public static class Login {
        public static void login(Context context, String email, String password, Promise promise) {
            promise.resolving(0, null);

            String url = ApiKey.REQUEST_API_URL + "account/login/";
            RequestQueue queue = Volley.newRequestQueue(context);
            promise.resolving(33, null);

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

            promise.resolving(75, null);
            queue.add(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        promise.resolving(100, null);
                        promise.resolved(response);
                    },
                    error -> promise.reject("unable to Login!")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("RAK", ApiKey.REQUEST_API_KEY);
                    return headers;
                }
            });
        }
    }

    public static class Account {
        public static void profile(Context context, Promise promise) {
            promise.resolving(0, null);

            String url = ApiKey.REQUEST_API_URL + "account/profile/";
            RequestQueue queue = Volley.newRequestQueue(context);
            promise.resolving(33, null);

            promise.resolving(75, null);
            queue.add(new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        promise.resolving(100, null);
                        promise.resolved(response);
                    },
                    error -> promise.reject("unable to load Profile!")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("RAK", ApiKey.REQUEST_API_KEY);
                    headers.put("AT", AUTH_TOKEN);
                    headers.put("LT", LOGIN_TOKEN);
                    return headers;
                }
            });
        }
    }
}
