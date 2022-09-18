package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.common.settings.ApiKey;
import com.rotiking.client.utils.Promise;

public class StarterActivity extends AppCompatActivity {
    private LinearProgressIndicator initProgress;
    private ImageView textLogoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        initProgress = findViewById(R.id.init_progress);
        textLogoImg = findViewById(R.id.text_logo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AES128.loadEncryptionKey();
        ApiKey.loadServerApiKey(this, new Promise<Object>() {
            @Override
            public void resolving(int progress, String msg) {
                if (progress == 100) {
                    textLogoImg.setVisibility(View.VISIBLE);
                    initProgress.setVisibility(View.INVISIBLE);
                    return;
                }
                initProgress.setProgress(progress);
            }

            @Override
            public void resolved(Object o) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Intent intent = new Intent(StarterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }, 1000);
            }

            @Override
            public void reject(String err) {
                Toast.makeText(StarterActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}