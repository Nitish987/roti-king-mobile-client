package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.tabs.HomeFragment;
import com.rotiking.client.tabs.ProfileFragment;
import com.rotiking.client.tabs.RecentFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Auth.isUserAuthenticated(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        tabs = findViewById(R.id.tabs);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        tabs.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    break;
                case R.id.recent:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();
                    break;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
            }
            return true;
        });
    }
}