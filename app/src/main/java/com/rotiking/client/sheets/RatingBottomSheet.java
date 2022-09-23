package com.rotiking.client.sheets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rotiking.client.R;

public class RatingBottomSheet extends BottomSheetDialogFragment {
    private View view;

    private String foodId = null;
    private double rating = 0;

    public static RatingBottomSheet newInstance(String foodId, double rating) {
        RatingBottomSheet fragment = new RatingBottomSheet();
        Bundle args = new Bundle();
        args.putString("FOOD_ID", foodId);
        args.putDouble("RATING", rating);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodId = getArguments().getString("FOOD_ID");
            rating = getArguments().getDouble("RATING");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_rating, container, false);

        return view;
    }
}