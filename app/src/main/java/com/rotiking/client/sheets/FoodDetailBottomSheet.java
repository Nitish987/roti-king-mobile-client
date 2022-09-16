package com.rotiking.client.sheets;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rotiking.client.R;
import com.rotiking.client.adapters.ToppingItemRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodDetailBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private ImageView photo;
    private TextView name, foodType, description, includes, crossPrice, price, discount, ingredients, rating, available, quantityTxt;
    private AppCompatImageButton incQuantityBtn, decQuantityBtn;
    private AppCompatButton priceBtn, cartBtn;
    private RecyclerView toppingsRV;

    private JSONObject food;
    public static JSONArray TOPPINGS;
    private int one_piece_price = 0;
    private int one_piece_price_before_discount = 0;
    private int quantity = 1;

    public static FoodDetailBottomSheet newInstance(String food) {
        FoodDetailBottomSheet fragment = new FoodDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString("FOOD", food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                food = new JSONObject(getArguments().getString("FOOD"));

                one_piece_price_before_discount = food.getInt("price");
                one_piece_price = food.getInt("price");
                if (food.getInt("discount") != 0) {
                    one_piece_price = one_piece_price - Math.round((float) (food.getInt("discount") * one_piece_price) / 100);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_food_detail, container, false);

        photo = view.findViewById(R.id.photo);
        name = view.findViewById(R.id.name);
        foodType = view.findViewById(R.id.food_type);
        description = view.findViewById(R.id.description);
        includes = view.findViewById(R.id.includes);
        price = view.findViewById(R.id.price);
        crossPrice = view.findViewById(R.id.cross_price);
        discount = view.findViewById(R.id.discount);
        ingredients = view.findViewById(R.id.ingredients);
        rating = view.findViewById(R.id.rating);
        available = view.findViewById(R.id.available);
        quantityTxt = view.findViewById(R.id.quantity);
        incQuantityBtn = view.findViewById(R.id.inc_quantity);
        decQuantityBtn = view.findViewById(R.id.dec_quantity);
        priceBtn = view.findViewById(R.id.price_order_btn);
        cartBtn = view.findViewById(R.id.cart_btn);

        crossPrice.setPaintFlags(crossPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        toppingsRV = view.findViewById(R.id.toppings_rv);
        toppingsRV.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        toppingsRV.hasFixedSize();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (!food.getString("photo").equals("")) {
                Glide.with(view.getContext()).load(food.getString("photo")).into(photo);
            }
            name.setText(food.getString("name"));
            foodType.setText(food.getString("food_type"));
            available.setText(food.getBoolean("available") ? "Available" : "Not Available right now. Come back Later");

            String rate_ = Double.toString(food.getDouble("rating"));
            rating.setText(rate_);

            String desc_ = "Description - \n" + food.getString("description");
            description.setText(desc_);

            String ing_ = "Ingredients - " + food.getString("ingredients");
            ingredients.setText(ing_);

            String inc_ = "Includes - " + food.getString("food_includes");
            includes.setText(inc_);


            if (food.getDouble("discount") == 0) {
                discount.setVisibility(View.INVISIBLE);
                crossPrice.setVisibility(View.GONE);
            } else {
                String dis_ = "-" + food.getDouble("discount") + "% OFF";
                discount.setText(dis_);

                setCrossPrice();
            }

            setPrice();

            ToppingItemRecyclerAdapter adapter = new ToppingItemRecyclerAdapter(TOPPINGS);
            toppingsRV.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        incQuantityBtn.setOnClickListener(view1 -> {
            quantity += 1;
            setQuantity();
            setPrice();
            setCrossPrice();
        });

        decQuantityBtn.setOnClickListener(view1 -> {
            quantity -= 1;
            if (quantity < 1) quantity = 1;
            setQuantity();
            setPrice();
            setCrossPrice();
        });
    }

    private void setPrice() {
        String pri_ = "\u20B9 " + (quantity * one_piece_price);
        price.setText(pri_);
    }

    private void setCrossPrice() {
        String cross_pri_ = "\u20B9 " + (quantity * one_piece_price_before_discount);
        crossPrice.setText(cross_pri_);
    }

    private void setQuantity() {
        String qua_ = Integer.toString(quantity);
        quantityTxt.setText(qua_);
    }
}