package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.adapters.CartItemRecyclerAdapter;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.models.CartItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private AppCompatButton checkoutBtn;
    private TextView totalCartPriceTxt;
    private CircularProgressIndicator cartItemProgress;
    private LinearLayout emptyCartI;

    private List<CartItem> cartItems;
    private int total_cart_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_cart_price);
        checkoutBtn = findViewById(R.id.checkout);
        cartItemProgress = findViewById(R.id.cart_item_progress);
        emptyCartI = findViewById(R.id.empty_cart_i);

        cartItemRV = findViewById(R.id.cart_item_rv);
        cartItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartItemRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartItemProgress.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("cart").get().addOnSuccessListener(queryDocumentSnapshots -> {
            cartItemProgress.setVisibility(View.GONE);

            cartItems = queryDocumentSnapshots.toObjects(CartItem.class);

            if (cartItems.isEmpty()) {
                emptyCartI.setVisibility(View.VISIBLE);
            }

            for (CartItem item: cartItems) {
                total_cart_price += item.getTotal_price();
            }

            String pri_ = "\u20B9 " + total_cart_price;
            totalCartPriceTxt.setText(pri_);

            CartItemRecyclerAdapter adapter = new CartItemRecyclerAdapter(cartItems, total_cart_price, o1 -> {
                total_cart_price = (int) o1[0];
                String newPrice = "\u20B9 " + total_cart_price;
                totalCartPriceTxt.setText(newPrice);
            });
            cartItemRV.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            cartItemProgress.setVisibility(View.GONE);
            Toast.makeText(CartActivity.this, "unable to load cart.", Toast.LENGTH_SHORT).show();
        });

        checkoutBtn.setOnClickListener(view -> {
            List<CartItem> newCartList = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (!CartItemRecyclerAdapter.REMOVED_CART_ITEM.contains(item.getItem_id())) {
                    newCartList.add(item);
                }
            }
            if (newCartList.isEmpty()) {
                Toast.makeText(this, "Please select or add some items to your cart before Checkout.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, CheckoutActivity.class);
                intent.putExtra("CART_ITEM", (Serializable) newCartList);
                intent.putExtra("TOTAL_CART_PRICE", total_cart_price);
                startActivity(intent);
                finish();
            }
        });

        closeBtn.setOnClickListener(view -> finish());
    }
}