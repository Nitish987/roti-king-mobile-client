package com.rotiking.client.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.rotiking.client.R;
import com.rotiking.client.adapters.FoodCardRecyclerAdapter;
import com.rotiking.client.adapters.FoodItemRecyclerAdapter;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.models.Food;
import com.rotiking.client.sheets.FoodDetailBottomSheet;
import com.rotiking.client.utils.Promise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView foodsCardRV, foodsRV;
    private CircularProgressIndicator foodCardProgress;
    private ChipGroup foodFilters;

    private JSONArray foods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        foodsCardRV = view.findViewById(R.id.food_cards_rv);
        foodsCardRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        foodsCardRV.setHasFixedSize(true);

        foodsRV = view.findViewById(R.id.food_item_rv);
        foodsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        foodsRV.setHasFixedSize(true);

        foodCardProgress = view.findViewById(R.id.food_card_progress);
        foodFilters = view.findViewById(R.id.food_filter);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onStart() {
        super.onStart();
        Database.getFoodItems(view.getContext(), new Promise() {
            @Override
            public void resolving(int progress, String msg) {
                foodCardProgress.setVisibility(View.VISIBLE);
                foodFilters.setEnabled(false);
            }

            @Override
            public void resolved(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    if (response.getBoolean("success")) {
                        foods = response.getJSONObject("data").getJSONArray("foods");

                        foodCardProgress.setVisibility(View.GONE);
                        foodFilters.setEnabled(true);

                        FoodCardRecyclerAdapter cardAdapter = new FoodCardRecyclerAdapter(foods, getParentFragmentManager());
                        foodsCardRV.setAdapter(cardAdapter);

                        JSONArray foodItems = performTop10FoodQuery();
                        FoodItemRecyclerAdapter itemAdapter = new FoodItemRecyclerAdapter(foodItems, getParentFragmentManager());
                        foodsRV.setAdapter(itemAdapter);
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
                Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        foodFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            switch (group.getCheckedChipId()) {
                case R.id.breakfast:
                    JSONArray breakfast = performFoodCardQuery("breakfast");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(breakfast, getParentFragmentManager()), true);
                    break;

                case R.id.lunch:
                    JSONArray lunch = performFoodCardQuery("lunch");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(lunch, getParentFragmentManager()), true);
                    break;

                case R.id.dinner:
                    JSONArray dinner = performFoodCardQuery("dinner");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(dinner, getParentFragmentManager()), true);
                    break;

                default:
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(foods, getParentFragmentManager()), true);
                    break;
            }
        });

        Database.getToppingItems(view.getContext(), new Promise() {
            @Override
            public void resolving(int progress, String msg) {}

            @Override
            public void resolved(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    if (response.getBoolean("success")) {
                        FoodDetailBottomSheet.TOPPINGS = response.getJSONObject("data").getJSONArray("toppings");
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
                Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JSONArray performFoodCardQuery(String filter) {
        JSONArray query = new JSONArray();
        for (int i = 0; i < foods.length(); i++) {
            try {
                JSONObject f = foods.getJSONObject(i);
                if (f.getString("food_type").equals(filter)) {
                    query.put(f);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return query;
    }

    private JSONArray performTop10FoodQuery() {
        int limit = 10;
        JSONArray query = new JSONArray();
        for (int i = 0; i < foods.length(); i++) {
            try {
                if (limit == 0) break;

                JSONObject f = foods.getJSONObject(i);
                if (f.getDouble("rating") >= 3.5) {
                    query.put(f);
                    limit--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return query;
    }
}