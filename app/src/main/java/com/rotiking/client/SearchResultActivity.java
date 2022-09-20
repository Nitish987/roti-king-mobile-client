package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.rotiking.client.adapters.FoodItemRecyclerAdapter;
import com.rotiking.client.models.Food;
import com.rotiking.client.utils.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView foodsRV;
    private ChipGroup foodFilters;
    private ImageButton closeBtn;
    private CircularProgressIndicator foodSearchProgress;

    private String query;
    private List<Food> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        foodsRV = findViewById(R.id.food_item_rv);
        foodsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        foodsRV.setHasFixedSize(true);

        foodFilters = findViewById(R.id.food_filter);
        closeBtn = findViewById(R.id.close);
        foodSearchProgress = findViewById(R.id.food_item_progress);

        query = getIntent().getStringExtra("SEARCH_QUERY");
        foods = (List<Food>) getIntent().getSerializableExtra("FOOD_DATA");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        performSearchQuery(query, new Promise<List<Food>>() {
            @Override
            public void resolving(int progress, String msg) {
                foodFilters.setEnabled(false);
                foodSearchProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void resolved(List<Food> foods) {
                foodFilters.setEnabled(true);
                foodSearchProgress.setVisibility(View.INVISIBLE);

                FoodItemRecyclerAdapter itemAdapter = new FoodItemRecyclerAdapter(foods, getSupportFragmentManager());
                foodsRV.setAdapter(itemAdapter);
            }

            @Override
            public void reject(String err) {
                foodFilters.setEnabled(false);
                Toast.makeText(SearchResultActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });

        foodFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            switch (group.getCheckedChipId()) {
                case R.id.breakfast:
                    List<Food> breakfast = performFoodQueryFilter("breakfast");
                    foodsRV.swapAdapter(new FoodItemRecyclerAdapter(breakfast, getSupportFragmentManager()), true);
                    break;

                case R.id.lunch:
                    List<Food> lunch = performFoodQueryFilter("lunch");
                    foodsRV.swapAdapter(new FoodItemRecyclerAdapter(lunch, getSupportFragmentManager()), true);
                    break;

                case R.id.dinner:
                    List<Food> dinner = performFoodQueryFilter("dinner");
                    foodsRV.swapAdapter(new FoodItemRecyclerAdapter(dinner, getSupportFragmentManager()), true);
                    break;

                default:
                    foodsRV.swapAdapter(new FoodItemRecyclerAdapter(foods, getSupportFragmentManager()), true);
                    break;
            }
        });

        closeBtn.setOnClickListener(view -> finish());
    }

    private List<Food> performFoodQueryFilter(String filter) {
        return foods.stream().filter(f -> f.getFood_type().equals(filter)).collect(Collectors.toList());
    }

    private void performSearchQuery(String query, Promise<List<Food>> promise) {
        try {
            promise.resolving(0, null);
            String[] search = query.split(" ");
            List<Food> foodQuery = new ArrayList<>();
            for (String s : search) {
                promise.resolving(50, null);
                foods.stream().filter(f -> f.getName().startsWith(s) || f.getName().endsWith(s) || f.getFood_type().startsWith(s) || f.getFood_type().endsWith(s)).forEach(f -> {
                    if (!foodQuery.contains(f)) {
                        foodQuery.add(f);
                    }
                });
            }
            this.foods = foodQuery;
            promise.resolved(foodQuery);
        } catch (Exception e) {
            promise.reject("Something went wrong.");
        }
    }
}