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
import com.rotiking.client.adapters.FoodCardRecyclerAdapter;
import com.rotiking.client.adapters.FoodItemRecyclerAdapter;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.models.Food;
import com.rotiking.client.utils.Promise;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView foodsRV;
    private CircularProgressIndicator foodSearchProgress;
    private ChipGroup foodFilters;
    private ImageButton closeBtn;

    private String query;
    private List<Food> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        foodsRV = findViewById(R.id.food_item_rv);
        foodsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        foodsRV.setHasFixedSize(true);

        foodSearchProgress = findViewById(R.id.food_item_progress);
        foodFilters = findViewById(R.id.food_filter);
        closeBtn = findViewById(R.id.close);

        query = getIntent().getStringExtra("SEARCH_QUERY");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        Database.searchFoodItem(this, query, new Promise<List<Food>>() {
            @Override
            public void resolving(int progress, String msg) {
                foodSearchProgress.setVisibility(View.VISIBLE);
                foodFilters.setEnabled(false);
            }

            @Override
            public void resolved(List<Food> foods_) {
                foods = foods_;
                foodSearchProgress.setVisibility(View.GONE);
                foodFilters.setEnabled(true);

                FoodItemRecyclerAdapter itemAdapter = new FoodItemRecyclerAdapter(foods, getSupportFragmentManager());
                foodsRV.setAdapter(itemAdapter);
            }

            @Override
            public void reject(String err) {
                Toast.makeText(SearchResultActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });

        foodFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            switch (group.getCheckedChipId()) {
                case R.id.breakfast:
                    List<Food> breakfast = performFoodCardQuery("breakfast");
                    foodsRV.swapAdapter(new FoodCardRecyclerAdapter(breakfast, getSupportFragmentManager()), true);
                    break;

                case R.id.lunch:
                    List<Food> lunch = performFoodCardQuery("lunch");
                    foodsRV.swapAdapter(new FoodCardRecyclerAdapter(lunch, getSupportFragmentManager()), true);
                    break;

                case R.id.dinner:
                    List<Food> dinner = performFoodCardQuery("dinner");
                    foodsRV.swapAdapter(new FoodCardRecyclerAdapter(dinner, getSupportFragmentManager()), true);
                    break;

                default:
                    foodsRV.swapAdapter(new FoodCardRecyclerAdapter(foods, getSupportFragmentManager()), true);
                    break;
            }
        });

        closeBtn.setOnClickListener(view -> finish());
    }

    private List<Food> performFoodCardQuery(String filter) {
        return foods.stream().filter(f -> f.getFood_type().equals(filter)).collect(Collectors.toList());
    }
}