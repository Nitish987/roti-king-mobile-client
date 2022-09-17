package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rotiking.client.adapters.CartItemRecyclerAdapter;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.utils.Promise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartItemRV;
    private ImageButton closeBtn;
    private TextView totalCartPriceTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        closeBtn = findViewById(R.id.close);
        totalCartPriceTxt = findViewById(R.id.total_cart_price);

        cartItemRV = findViewById(R.id.cart_item_rv);
        cartItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        cartItemRV.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeBtn.setOnClickListener(view -> finish());

        Database.getCartItems(this, new Promise() {
            @Override
            public void resolving(int progress, String msg) {

            }

            @Override
            public void resolved(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray cartItems = data.getJSONArray("cart");
                        int total_cart_price = data.getInt("total_cart_price");

                        String pri_ = "\u20B9 " + total_cart_price;
                        totalCartPriceTxt.setText(pri_);

                        CartItemRecyclerAdapter adapter = new CartItemRecyclerAdapter(cartItems, o1 -> {
                            int price = (int) o1[0];
                            boolean isToBeAdded = (boolean) o1[1];
                            String newPrice;
                            if (isToBeAdded) {
                                newPrice = "\u20B9 " + (total_cart_price + price);
                            } else {
                                newPrice = "\u20B9 " + (total_cart_price - price);
                            }
                            totalCartPriceTxt.setText(newPrice);
                        });
                        cartItemRV.setAdapter(adapter);
                    } else {
                        JSONObject errors = response.getJSONObject("data").getJSONObject("errors");
                        String key = errors.keys().next();
                        JSONArray array = errors.getJSONArray(key);

                        Toast.makeText(CartActivity.this, array.getString(0), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CartActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void reject(String err) {
                Toast.makeText(CartActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}