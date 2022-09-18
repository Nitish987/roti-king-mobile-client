package com.rotiking.client.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Server {
    public static void request(Context context, int method, String url, Map<String, String> headers, JSONObject body, Promise<JSONObject> promise) {
        promise.resolving(0, "Getting things ready...");

        RequestQueue queue = Volley.newRequestQueue(context);
        promise.resolving(50, "Request in queue...");

        queue.add(new JsonObjectRequest(
                method,
                url,
                body,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            promise.resolving(100, "Done...");
                            promise.resolved(response.getJSONObject("data"));
                        } else {
                            promise.reject(Response.extractJsonErrorMessage(response.getJSONObject("data").getJSONObject("errors")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        promise.reject("Something went wrong.");
                    }
                },
                error -> promise.reject("unable to Connect to Server!")
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        });
    }

    public static class Response {
        public static String extractJsonErrorMessage(JSONObject errors) {
            try {
                String key = errors.keys().next();
                JSONArray array = errors.getJSONArray(key);
                return array.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
                return "Something went wrong.";
            }
        }
    }
}
