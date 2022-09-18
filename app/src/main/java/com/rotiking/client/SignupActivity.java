package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private AppCompatButton continueBtn;
    private TextView loginTxt;
    private EditText name_eTxt, email_eTxt;
    private ProgressBar continueProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        continueBtn = findViewById(R.id.continue_btn);
        loginTxt = findViewById(R.id.login_txt);
        name_eTxt = findViewById(R.id.name_e_txt);
        email_eTxt = findViewById(R.id.email_e_txt);
        continueProgress = findViewById(R.id.continue_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        continueBtn.setOnClickListener(view -> {
            String name = name_eTxt.getText().toString();
            String email = email_eTxt.getText().toString();

            if (Validator.isEmpty(name)) {
                name_eTxt.setError("Required Field");
                return;
            }

            if (!Validator.isMinLength(name, 3)) {
                name_eTxt.setError("Name should be of 3 character at least.");
                return;
            }

            if (Validator.isEmpty(email)) {
                email_eTxt.setError("Required Field");
                return;
            }

            if (!Validator.isEmail(email)) {
                email_eTxt.setError("Invalid Email");
                return;
            }

            continueBtn.setVisibility(View.INVISIBLE);

            Auth.Signup.signup(this, name, email, new Promise<JSONObject>() {
                @Override
                public void resolving(int progress, String msg) {
                    continueProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void resolved(JSONObject data) {
                    try {
                        String message = data.getString("message");
                        String token = data.getString("token");

                        continueProgress.setVisibility(View.INVISIBLE);
                        continueBtn.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(SignupActivity.this, SignupPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("message", message);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                        continueBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void reject(String err) {
                    Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                    continueBtn.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}