package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartItemRv;
    private ImageButton closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        closeBtn = findViewById(R.id.close);

        cartItemRv = findViewById(R.id.cart_item_rv);
        cartItemRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cartItemRv.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeBtn.setOnClickListener(view -> finish());


    }
}