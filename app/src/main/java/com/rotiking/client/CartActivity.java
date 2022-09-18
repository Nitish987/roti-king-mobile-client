package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rotiking.client.adapters.CartItemRecyclerAdapter;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.utils.Promise;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private AppCompatButton checkoutBtn;
    private TextView totalCartPriceTxt;

    private List<CartItem> cartItems;
    private int total_cart_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_cart_price);
        checkoutBtn = findViewById(R.id.checkout);

        cartItemRV = findViewById(R.id.cart_item_rv);
        cartItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartItemRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeBtn.setOnClickListener(view -> finish());

        Database.getCartItems(this, new Promise<JSONObject>() {
            @Override
            public void resolving(int progress, String msg) {

            }

            @Override
            public void resolved(JSONObject data) {
                try {
                    Gson gson = new Gson();
                    cartItems = Arrays.asList(gson.fromJson(data.getJSONArray("cart").toString(), CartItem[].class));
                    total_cart_price = data.getInt("total_cart_price");

                    String pri_ = "\u20B9 " + total_cart_price;
                    totalCartPriceTxt.setText(pri_);

                    CartItemRecyclerAdapter adapter = new CartItemRecyclerAdapter(cartItems, total_cart_price, o1 -> {
                        total_cart_price = (int) o1[0];
                        String newPrice = "\u20B9 " + total_cart_price;
                        totalCartPriceTxt.setText(newPrice);
                    });
                    cartItemRV.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void reject(String err) {
                Toast.makeText(CartActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });

        checkoutBtn.setOnClickListener(view -> {
            List<CartItem> newCartList = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (!CartItemRecyclerAdapter.REMOVED_CART_ITEM.contains(item.getItem_id())) {
                    newCartList.add(item);
                }
            }
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("CART_ITEM", (Serializable) newCartList);
            intent.putExtra("TOTAL_CART_PRICE", total_cart_price);
            startActivity(intent);
        });
    }
}