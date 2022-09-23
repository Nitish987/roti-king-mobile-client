package com.rotiking.client.sheets;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rotiking.client.R;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.utils.Promise;

public class RatingBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private TextView foodNameTxt;
    private AppCompatRatingBar ratingBar;
    private AppCompatButton saveRatingBtn;

    private String foodId = null;
    private String foodName = null;

    public static RatingBottomSheet newInstance(String foodId, String foodName) {
        RatingBottomSheet fragment = new RatingBottomSheet();
        Bundle args = new Bundle();
        args.putString("FOOD_ID", foodId);
        args.putString("FOOD_NAME", foodName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodId = getArguments().getString("FOOD_ID");
            foodName = getArguments().getString("FOOD_NAME");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_rating, container, false);

        foodNameTxt = view.findViewById(R.id.food_name);
        ratingBar = view.findViewById(R.id.rating_bar);
        saveRatingBtn = view.findViewById(R.id.save_rating_btn);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        foodNameTxt.setText(foodName);

        Database.getRating(view.getContext(), foodId, new Promise<Double>() {
            @Override
            public void resolving(int progress, String msg) {}

            @Override
            public void resolved(Double rating) {
                ratingBar.setRating(rating.floatValue());
            }

            @Override
            public void reject(String err) {
                Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        saveRatingBtn.setOnClickListener(view1 -> {
            Database.setRating(view.getContext(), foodId, ratingBar.getRating(), new Promise<String>() {
                @Override
                public void resolving(int progress, String msg) {}

                @Override
                public void resolved(String msg) {
                    Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void reject(String err) {
                    Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}