package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rotiking.client.adapters.CheckoutCartItemRecyclerAdapter;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.CheckoutCartItem;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private TextView totalCartPriceTxt, deliveryCartPriceTxt, totalPayableTxt;
    private AppCompatButton orderBtn;

    private List<CartItem> cartItems;
    private int total_cart_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartItems = (List<CartItem>) getIntent().getSerializableExtra("CART_ITEM");
        total_cart_price = getIntent().getIntExtra("TOTAL_CART_PRICE", 0);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_price);
        deliveryCartPriceTxt = findViewById(R.id.delivery_price);
        totalPayableTxt = findViewById(R.id.payable_price);
        orderBtn = findViewById(R.id.place_order);

        cartItemRV = findViewById(R.id.cart_item_rv);
        cartItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartItemRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckoutCartItemRecyclerAdapter adapter = new CheckoutCartItemRecyclerAdapter(createOrderItemList());
        cartItemRV.setAdapter(adapter);

        String tcp = "\u20B9 " + total_cart_price;
        totalCartPriceTxt.setText(tcp);

        String dp = "\u20B9 " + 0;
        deliveryCartPriceTxt.setText(dp);

        String tp = "\u20B9 " + (total_cart_price);
        totalPayableTxt.setText(tp);

        orderBtn.setOnClickListener(view -> {
            // TODO
        });

        closeBtn.setOnClickListener(view -> finish());
    }

    private List<CheckoutCartItem> createOrderItemList() {
        List<CheckoutCartItem> checkoutCartItems = new ArrayList<>();
        for (CartItem cartItem: cartItems) {
            String orderName = cartItem.getFood_data().getName();
            if (!cartItem.getTopping_ids().equals("None")) {
                orderName = orderName + " + Toppings";
            }
            CheckoutCartItem item = new CheckoutCartItem(orderName, cartItem.getTotal_price());
            checkoutCartItems.add(item);
        }
        return checkoutCartItems;
    }
}