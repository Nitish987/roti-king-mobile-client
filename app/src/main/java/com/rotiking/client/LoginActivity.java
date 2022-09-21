package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.auth.AuthPreferences;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton loginBtn;
    private TextView signupTxt;
    private EditText email_eTxt, password_eTxt;
    private CircularProgressIndicator loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_eTxt = findViewById(R.id.email_e_txt);
        password_eTxt = findViewById(R.id.password_e_txt);
        loginBtn = findViewById(R.id.login_btn);
        signupTxt = findViewById(R.id.signup_txt);
        loginProgress = findViewById(R.id.login_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signupTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(view -> {
            String email = email_eTxt.getText().toString();
            String password = password_eTxt.getText().toString();

            if (Validator.isEmpty(email)) {
                email_eTxt.setError("Required Field");
                return;
            }

            if (!Validator.isEmail(email)) {
                email_eTxt.setError("Invalid Email");
                return;
            }

            if (Validator.isEmpty(password)) {
                password_eTxt.setError("Required Field");
                return;
            }

            Object[] isPassword = Validator.isPassword(password);

            if (!((boolean) isPassword[0])) {
                String err = (String) isPassword[1];
                password_eTxt.setError(err);
                return;
            }

            loginBtn.setVisibility(View.INVISIBLE);

            Auth.Login.login(this, email, password, new Promise<JSONObject>() {
                @Override
                public void resolving(int progress, String msg) {
                    loginProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void resolved(JSONObject data) {
                    try {
                        String message = data.getString("message");
                        String fToken = data.getString("fToken");
                        String token = data.getString("token");
                        String login = data.getString("login");
                        String encKey = data.getString("encKey");

                        encKey = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, encKey);

                        String finalEncKey = encKey;
                        FirebaseAuth.getInstance().signInWithCustomToken(fToken).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AuthPreferences authPreferences = new AuthPreferences(LoginActivity.this);
                                authPreferences.setAuthToken(token, login);
                                authPreferences.setEncryptionKey(finalEncKey);

                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Unable to Login.", Toast.LENGTH_LONG).show();
                                loginBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                        loginBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void reject(String err) {
                    Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT).show();
                    loginBtn.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}