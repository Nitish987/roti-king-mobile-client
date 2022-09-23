package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.tabs.HomeFragment;
import com.rotiking.client.tabs.ProfileFragment;
import com.rotiking.client.tabs.OrdersFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView tabs;
    private ImageButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Auth.isUserAuthenticated(this)) {
            toLoginActivity();
        }

        tabs = findViewById(R.id.tabs);
        cartBtn = findViewById(R.id.cart);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        Auth.setAuthStateListener(firebaseAuth -> {
            if (!Auth.isUserAuthenticated(this)) {
                toLoginActivity();
            }
        });

        tabs.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    break;
                case R.id.orders:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                    break;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
            }
            return true;
        });

        cartBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}