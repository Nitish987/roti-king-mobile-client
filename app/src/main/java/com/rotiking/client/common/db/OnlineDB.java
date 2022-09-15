package com.rotiking.client.common.db;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.settings.ApiKey;
import com.rotiking.client.utils.Promise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OnlineDB {
    public static void readFoodItems(Context context, Promise promise) {
        promise.resolving(0, null);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(
                Request.Method.GET,
                ApiKey.REQUEST_API_URL + "client/foods/",
                null,
                response -> {
                    promise.resolving(100, null);
                    promise.resolved(response);
                },
                error -> promise.reject(error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("RAK", ApiKey.REQUEST_API_KEY);
                headers.put("AT", Auth.AUTH_TOKEN);
                headers.put("LT", Auth.LOGIN_TOKEN);
                return headers;
            }
        });
    }
}
