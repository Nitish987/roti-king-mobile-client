package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.utils.Promise;

import java.util.Objects;

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
        Thread thread = new Thread(() -> FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("cart").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot: queryDocumentSnapshots) {
                FirebaseFirestore.getInstance().collection("user").document(Auth.getAuthUserUid()).collection("cart").document(snapshot.getId()).delete();
            }
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