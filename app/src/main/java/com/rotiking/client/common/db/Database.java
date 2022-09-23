package com.rotiking.client.common.db;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.settings.ApiKey;
import com.rotiking.client.models.Food;
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
    public static void getFoodItems(Context context, Promise<List<Food>> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

        Server.request(context, Request.Method.GET, ApiKey.REQUEST_API_URL + "client/foods/", headers, null, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        promise.resolving(progress, msg);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        Gson gson = new Gson();
                        try {
                            Food[] foods = gson.fromJson(data.getJSONArray("foods").toString(), Food[].class);
                            promise.resolved(Arrays.asList(foods));
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

    public static void getToppingItems(Context context, Promise<List<Topping>> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

        Server.request(context, Request.Method.GET, ApiKey.REQUEST_API_URL + "client/toppings/", headers, null, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        promise.resolving(progress, msg);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        Gson gson = new Gson();
                        try {
                            Topping[] toppings = gson.fromJson(data.getJSONArray("toppings").toString(), Topping[].class);
                            promise.resolved(Arrays.asList(toppings));
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

    public static void addToCart(Context context, String foodId, int quantity, List<String> toppingIds, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

        JSONObject cartItem = new JSONObject();
        try {
            cartItem.put("food_id", foodId);
            cartItem.put("quantity", quantity);
            cartItem.put("topping_ids", toppingIds.isEmpty() ? "None" : String.join(",", toppingIds));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "client/add-to-cart/", headers, cartItem, new Promise<JSONObject>() {
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

    public static void getCartItems(Context context, Promise<JSONObject> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

        Server.request(context, Request.Method.GET, ApiKey.REQUEST_API_URL + "client/my-cart-list/", headers, null, new Promise<JSONObject>() {
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

    public static void clearCartItems(Context context, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

        Server.request(context, Request.Method.DELETE, ApiKey.REQUEST_API_URL + "client/clear-my-cart/", headers, null, new Promise<JSONObject>() {
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

    public static void setRating(Context context, String foodId, double rating, Promise<String> promise) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RAK", ApiKey.REQUEST_API_KEY);
        headers.put("AT", Auth.AUTH_TOKEN);
        headers.put("LT", Auth.LOGIN_TOKEN);

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
        headers.put("LT", Auth.LOGIN_TOKEN);

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
}
