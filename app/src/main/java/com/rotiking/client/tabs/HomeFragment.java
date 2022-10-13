package com.rotiking.client.tabs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.R;
import com.rotiking.client.SearchResultActivity;
import com.rotiking.client.adapters.FoodCardRecyclerAdapter;
import com.rotiking.client.adapters.FoodItemRecyclerAdapter;
import com.rotiking.client.models.Food;
import com.rotiking.client.models.Topping;
import com.rotiking.client.sheets.FoodDetailBottomSheet;
import com.rotiking.client.utils.Validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView foodsCardRV, foodsRV;
    private CircularProgressIndicator foodCardProgress, foodItemProgress;
    private ChipGroup foodFilters;
    private EditText search_eTxt;
    private AppCompatImageButton searchBtn;
    private TextView license;

    private List<Food> foods;

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
        foodItemProgress = view.findViewById(R.id.food_item_progress);
        foodFilters = view.findViewById(R.id.food_filter);
        search_eTxt = view.findViewById(R.id.search);
        searchBtn = view.findViewById(R.id.search_btn);
        license = view.findViewById(R.id.license);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("foods").get().addOnSuccessListener(requireActivity(), queryDocumentSnapshots -> {
            foods = queryDocumentSnapshots.toObjects(Food.class);

            foodCardProgress.setVisibility(View.GONE);
            foodItemProgress.setVisibility(View.GONE);
            foodFilters.setEnabled(true);

            FoodCardRecyclerAdapter cardAdapter = new FoodCardRecyclerAdapter(foods, getParentFragmentManager());
            foodsCardRV.setAdapter(cardAdapter);

            List<Food> foodItems = performTop10FoodQuery();
            FoodItemRecyclerAdapter itemAdapter = new FoodItemRecyclerAdapter(foodItems, getParentFragmentManager());
            foodsRV.setAdapter(itemAdapter);
        }).addOnFailureListener(e -> Toast.makeText(view.getContext(), "Unable to load Item.", Toast.LENGTH_SHORT).show());

        foodFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            switch (group.getCheckedChipId()) {
                case R.id.breakfast:
                    List<Food> breakfast = performFoodCardQuery("breakfast");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(breakfast, getParentFragmentManager()), true);
                    break;

                case R.id.lunch:
                    List<Food> lunch = performFoodCardQuery("lunch");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(lunch, getParentFragmentManager()), true);
                    break;

                case R.id.dinner:
                    List<Food> dinner = performFoodCardQuery("dinner");
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(dinner, getParentFragmentManager()), true);
                    break;

                default:
                    foodsCardRV.swapAdapter(new FoodCardRecyclerAdapter(foods, getParentFragmentManager()), true);
                    break;
            }
        });

        FirebaseFirestore.getInstance().collection("toppings").get().addOnSuccessListener(requireActivity(), queryDocumentSnapshots -> FoodDetailBottomSheet.TOPPINGS = queryDocumentSnapshots.toObjects(Topping.class)).addOnFailureListener(e -> Toast.makeText(view.getContext(), "Unable to load Item.", Toast.LENGTH_SHORT).show());

        searchBtn.setOnClickListener(view1 -> {
            String query = search_eTxt.getText().toString();

            if (Validator.isEmpty(query)) {
                Toast.makeText(view.getContext(), "Search should not be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(view.getContext(), SearchResultActivity.class);
            intent.putExtra("SEARCH_QUERY", query);
            intent.putExtra("FOOD_DATA", (Serializable) foods);
            startActivity(intent);
        });

        FirebaseFirestore.getInstance().collection("app").document("foodLicense").get().addOnSuccessListener(documentSnapshot -> {
            String foodLicense = documentSnapshot.get("license", String.class);
            license.setText(foodLicense);
        });
    }

    private List<Food> performFoodCardQuery(String filter) {
        return foods.stream().filter(f -> f.getFood_type().equals(filter)).collect(Collectors.toList());
    }

    private List<Food> performTop10FoodQuery() {
        int limit = 10;
        List<Food> query = new ArrayList<>();
        for (int i = 0; i < foods.size(); i++) {
            if (limit == 0) break;

            Food f = foods.get(i);
            if (f.getRating() >= 3.5) {
                query.add(f);
                limit--;
            }
        }
        return query;
    }
}