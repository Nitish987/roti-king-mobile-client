package com.rotiking.client.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.rotiking.client.R;
import com.rotiking.client.adapters.FoodCardRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.db.OnlineDB;
import com.rotiking.client.common.settings.ApiKey;
import com.rotiking.client.utils.Promise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView foodsRV;
    private CircularProgressIndicator foodCardProgress;
    private ChipGroup foodFilters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        foodsRV = view.findViewById(R.id.food_cards_rv);
        foodsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        foodsRV.setHasFixedSize(true);

        foodCardProgress = view.findViewById(R.id.food_card_progress);
        foodFilters = view.findViewById(R.id.food_filter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        OnlineDB.readFoodItems(view.getContext(), new Promise() {
            @Override
            public void resolving(int progress, String msg) {
                foodCardProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void resolved(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    if (response.getBoolean("success")) {
                        JSONArray foods = response.getJSONObject("data").getJSONArray("foods");
                        foodCardProgress.setVisibility(View.GONE);

                        FoodCardRecyclerAdapter adapter = new FoodCardRecyclerAdapter(foods);
                        foodsRV.setAdapter(adapter);
                    } else {
                        JSONObject errors = response.getJSONObject("data").getJSONObject("errors");
                        String key = errors.keys().next();
                        JSONArray array = errors.getJSONArray(key);

                        Toast.makeText(view.getContext(), array.getString(0), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void reject(String err) {
                Toast.makeText(view.getContext(), "something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });

        foodFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (group.getCheckedChipId() == R.id.all) {
                Log.e("TAG", "onStart: All");
            }
            if (group.getCheckedChipId() == R.id.breakfast) {
                Log.e("TAG", "onStart: Breakfast");
            }
            if (group.getCheckedChipId() == R.id.lunch) {
                Log.e("TAG", "onStart: Lunch");
            }
            if (group.getCheckedChipId() == R.id.dinner) {
                Log.e("TAG", "onStart: Dinner");
            }
        });
    }
}