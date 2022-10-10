package com.rotiking.client.common.db;

import android.content.Context;

import com.android.volley.Request;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.settings.ApiKey;
import com.rotiking.client.models.Food;
import com.rotiking.client.models.Order;
import com.rotiking.client.models.Topping;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }

    public static void setRating(Context context, String foodId, double rating, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("UID", Auth.getAuthUserUid());

        JSONObject rate = new JSONObject();
        try {
            rate.put("food_id", foodId);
            rate.put("rating", rating);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "client/set-food-rating/", headers, rate, new Promise<JSONObject>() {
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

    public static void getRating(Context context, String foodId, Promise<Double> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("UID", Auth.getAuthUserUid());

        Server.request(context, Request.Method.GET, ApiKey.REQUEST_API_URL + "client/get-food-rating/" + foodId + "/", headers, null, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        promise.resolving(progress, msg);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            promise.resolved(data.getDouble("rating"));
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

    public static void createRazorPayOrder(Context context, int amount, String currency, String receipt, Promise<JSONObject> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("UID", Auth.getAuthUserUid());

        JSONObject o = new JSONObject();
        try {
            o.put("amount", amount);
            o.put("currency", currency);
            o.put("receipt", receipt);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "client/razor-pay-order/", headers, o, new Promise<JSONObject>() {
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

    public static void createCustomerOrder(Context context, Order order, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("UID", Auth.getAuthUserUid());

        JSONObject o = new JSONObject();
        try {
            Gson gson = new Gson();
            String order_json = gson.toJson(order);

            o.put("order_json", order_json);
            o.put("razorpay_payment_id", razorpayPaymentId);
            o.put("razorpay_order_id", razorpayOrderId);
            o.put("razorpay_signature", razorpaySignature);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "client/customer-order/", headers, o, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        promise.resolving(progress, msg);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            promise.resolved(data.getString("order_id"));
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

    public static void cancelCustomerOrder(Context context, String orderId, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("UID", Auth.getAuthUserUid());

        Server.request(context, Request.Method.DELETE, ApiKey.REQUEST_API_URL + "client/cancel-customer-order/" + orderId + "/", headers, null, new Promise<JSONObject>() {
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
