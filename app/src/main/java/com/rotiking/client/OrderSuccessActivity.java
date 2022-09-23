package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rotiking.client.common.db.Database;
import com.rotiking.client.utils.Promise;

public class OrderSuccessActivity extends AppCompatActivity {
    private AppCompatButton viewOrderDetailsBtn;
    private ImageButton closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        viewOrderDetailsBtn = findViewById(R.id.view_order_details);
        closeBtn = findViewById(R.id.close);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(() -> Database.clearCartItems(this, new Promise<String>() {
            @Override
            public void resolving(int progress, String msg) {}

            @Override
            public void resolved(String o) {
                Toast.makeText(OrderSuccessActivity.this, "order Placed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void reject(String err) {}
        }));
        thread.start();

        viewOrderDetailsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("ORDER", getIntent().getStringExtra("ORDER"));
            startActivity(intent);
            finish();
        });

        closeBtn.setOnClickListener(view -> finish());
    }
}