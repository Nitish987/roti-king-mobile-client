package com.rotiking.client.sheets;

import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.CartActivity;
import com.rotiking.client.R;
import com.rotiking.client.adapters.ToppingItemRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.Food;
import com.rotiking.client.models.Topping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodDetailBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private ImageView photo;
    private TextView name, foodType, description, includes, crossPrice, price, discount, ingredients, rating, available, quantityTxt, payableTxt;
    private CircularProgressIndicator addToCartProgress;
    private AppCompatImageButton incQuantityBtn, decQuantityBtn;
    private AppCompatButton addToCartBtn, openCartBtn;
    private RecyclerView toppingsRV;

    private Food food;
    public static List<Topping> TOPPINGS;
    private int one_piece_price = 0;
    private int one_piece_price_before_discount = 0;
    private int topping_price = 0;
    private int quantity = 1;

    public static FoodDetailBottomSheet newInstance(Food food) {
        FoodDetailBottomSheet fragment = new FoodDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("FOOD", food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable("FOOD");

            one_piece_price_before_discount = food.getPrice();
            one_piece_price = food.getPrice();
            if (food.getDiscount() != 0) {
                one_piece_price = one_piece_price - Math.round((float) (food.getDiscount() * one_piece_price) / 100);
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
        payableTxt = view.findViewById(R.id.payable_price);
        incQuantityBtn = view.findViewById(R.id.inc_quantity);
        decQuantityBtn = view.findViewById(R.id.dec_quantity);
        addToCartBtn = view.findViewById(R.id.add_to_cart_btn);
        openCartBtn = view.findViewById(R.id.open_cart_btn);
        addToCartProgress = view.findViewById(R.id.add_to_cart_progress);

        crossPrice.setPaintFlags(crossPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        toppingsRV = view.findViewById(R.id.toppings_rv);
        toppingsRV.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        toppingsRV.hasFixedSize();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!food.getPhoto().equals("")) {
            Glide.with(view.getContext()).load(food.getPhoto()).into(photo);
        }
        name.setText(food.getName());
        foodType.setText(food.getFood_type());
        available.setText(food.isAvailable() ? "Available" : "Not Available right now. Come back Later");

        if (!food.isAvailable()) {
            addToCartBtn.setVisibility(View.INVISIBLE);
            addToCartBtn.setEnabled(false);
        }

        String rate_ = Double.toString(food.getRating());
        rating.setText(rate_);

        String desc_ = "Description - \n" + food.getDescription();
        description.setText(desc_);

        String ing_ = "Ingredients - " + food.getIngredients();
        ingredients.setText(ing_);

        String inc_ = "Includes - " + food.getFood_includes();
        includes.setText(inc_);

        if (food.getDiscount() == 0) {
            discount.setVisibility(View.INVISIBLE);
            crossPrice.setVisibility(View.GONE);
        } else {
            String dis_ = "-" + food.getDiscount() + "% OFF";
            discount.setText(dis_);

            setCrossPrice();
        }

        setPrice();
        setPayablePrice();

        ToppingItemRecyclerAdapter adapter = new ToppingItemRecyclerAdapter(TOPPINGS, o -> {
            topping_price = (Integer) o[0];
            setPayablePrice();
        });
        toppingsRV.setAdapter(adapter);

        incQuantityBtn.setOnClickListener(view1 -> {
            quantity += 1;
            setQuantity();
            setPrice();
            setCrossPrice();
            setPayablePrice();
        });

        decQuantityBtn.setOnClickListener(view1 -> {
            quantity -= 1;
            if (quantity < 1) quantity = 1;
            setQuantity();
            setPrice();
            setCrossPrice();
            setPayablePrice();
        });

        addToCartBtn.setOnClickListener(view1 -> {
            addToCartBtn.setVisibility(View.INVISIBLE);
            addToCartProgress.setVisibility(View.VISIBLE);

            List<String> toppingIds = new ArrayList<>(ToppingItemRecyclerAdapter.toppingIds);
            List<Topping> selectedToppings = TOPPINGS.stream().filter(topping -> toppingIds.contains(topping.getTopping_id())).collect(Collectors.toList());
            DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("cart").document();

            CartItem item = new CartItem();
            item.setFood_data(food);
            item.setFood_id(food.getFood_id());
            item.setFood_price(quantity * one_piece_price);
            item.setItem_id(reference.getId());
            item.setQuantity(quantity);
            item.setTopping_ids(toppingIds.isEmpty() ? "None" : String.join(",", toppingIds));
            item.setTopping_price(topping_price);
            item.setToppings(selectedToppings);
            item.setTotal_price((quantity * one_piece_price) + topping_price);

            reference.set(item).addOnSuccessListener(unused -> {
                addToCartBtn.setVisibility(View.VISIBLE);
                addToCartProgress.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Item added.", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                addToCartBtn.setVisibility(View.VISIBLE);
                addToCartProgress.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "unable to add.", Toast.LENGTH_SHORT).show();
            });
        });

        openCartBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), CartActivity.class);
            startActivity(intent);
            dismiss();
        });
    }

    private void setQuantity() {
        String qua_ = Integer.toString(quantity);
        quantityTxt.setText(qua_);
    }

    private void setPrice() {
        String pri_ = "\u20B9 " + (quantity * one_piece_price);
        price.setText(pri_);
    }

    private void setCrossPrice() {
        String cross_pri_ = "\u20B9 " + (quantity * one_piece_price_before_discount);
        crossPrice.setText(cross_pri_);
    }

    private void setPayablePrice() {
        String pri_ = "\u20B9 " + ((quantity * one_piece_price) + topping_price);
        payableTxt.setText(pri_);
    }
}