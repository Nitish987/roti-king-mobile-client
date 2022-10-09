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
import com.rotiking.client.common.db.Database;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton loginBtn;
    private TextView signupTxt, forgetPasswordTxt;
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
        forgetPasswordTxt = findViewById(R.id.forget_password);
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
            loginProgress.setVisibility(View.VISIBLE);

            Auth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                loginProgress.setVisibility(View.VISIBLE);
                Database.getInstance().collection("user").document(Objects.requireNonNull(authResult.getUser()).getUid())
                        .collection("data").document("profile").get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String authToken = documentSnapshot.get("authToken", String.class);
                                String encKey = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, documentSnapshot.get("encKey", String.class));
                                String payKey = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, documentSnapshot.get("payKey", String.class));

                                AuthPreferences preferences = new AuthPreferences(this);
                                preferences.setAuthToken(authToken);
                                preferences.setEncryptionKey(encKey);
                                preferences.setPaymentKey(payKey);

                                loginProgress.setVisibility(View.GONE);

                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }).addOnFailureListener(e -> {
                loginBtn.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        forgetPasswordTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecoverPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Auth.isUserAuthenticated(this)) {
            Auth.getMessaging().getToken().addOnSuccessListener(s -> Auth.Login.updateMessageToken(this, s, new Promise<String>() {
                @Override
                public void resolving(int progress, String msg) {
                }

                @Override
                public void resolved(String o) {
                }

                @Override
                public void reject(String err) {
                }
            }));
        }
    }
}