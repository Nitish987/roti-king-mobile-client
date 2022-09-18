package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONObject;

public class SignupPasswordActivity extends AppCompatActivity {

    private TextView titleTxt, messageTxt;
    private LinearLayout otpSection, passwordSection, confirmPasswordSection;
    private EditText otp_eTxt, password_eTxt, confirmPassword_eTxt;
    private AppCompatButton signupBtn;
    private CircularProgressIndicator signupProgress;

    private static String message = null;
    private static String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password);

        titleTxt = findViewById(R.id.title_txt);
        messageTxt = findViewById(R.id.message_txt);
        otpSection = findViewById(R.id.otp_section);
        passwordSection = findViewById(R.id.password_section);
        confirmPasswordSection = findViewById(R.id.confirm_password_section);
        otp_eTxt = findViewById(R.id.otp_e_txt);
        password_eTxt = findViewById(R.id.password_e_txt);
        confirmPassword_eTxt = findViewById(R.id.confirm_password_e_txt);
        signupBtn = findViewById(R.id.sign_up_btn);
        signupProgress = findViewById(R.id.sign_up_progress);

        message = getIntent().getStringExtra("message");
        token = getIntent().getStringExtra("token");
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageTxt.setText(message);

        otp_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 6 || charSequence.length() < 6) {
                    otp_eTxt.setError("Invalid OTP");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signupBtn.setOnClickListener(view -> {
            if (signupBtn.getTag().equals("otp")) {
                String otp = otp_eTxt.getText().toString();

                if (Validator.isEmpty(otp)) {
                    otp_eTxt.setError("Required Field");
                    return;
                }

                if (!Validator.isEqualLength(otp, 6)) {
                    otp_eTxt.setError("Invalid OTP.");
                    return;
                }

                signupBtn.setVisibility(View.INVISIBLE);

                Auth.Signup.signupOtpVerification(this, token, otp, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        signupProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            String message_ = data.getString("message");
                            String token_ = data.getString("token");

                            String title = "Create Password";
                            titleTxt.setText(title);
                            messageTxt.setText(message_);

                            token = token_;

                            otpSection.setVisibility(View.GONE);
                            passwordSection.setVisibility(View.VISIBLE);
                            confirmPasswordSection.setVisibility(View.VISIBLE);
                            signupProgress.setVisibility(View.INVISIBLE);
                            signupBtn.setVisibility(View.VISIBLE);

                            signupBtn.setTag("password");

                            String btnText = "Signup";
                            signupBtn.setText(btnText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SignupPasswordActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                            signupBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(SignupPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        signupBtn.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                String password = password_eTxt.getText().toString();
                String confirm_password = confirmPassword_eTxt.getText().toString();

                if (Validator.isEmpty(password)) {
                    password_eTxt.setError("Required Field");
                    return;
                }

                if (Validator.isEmpty(confirm_password)) {
                    confirmPassword_eTxt.setError("Required Field");
                    return;
                }

                Object[] isPassword = Validator.isPassword(password);

                if (!((boolean) isPassword[0])) {
                    String err = (String) isPassword[1];
                    password_eTxt.setError(err);
                    confirmPassword_eTxt.setError(err);
                    return;
                }

                if (!password.equals(confirm_password)) {
                    password_eTxt.setError("Password does Not Match.");
                    confirmPassword_eTxt.setError("Password does Not Match.");
                    return;
                }

                signupBtn.setVisibility(View.INVISIBLE);

                Auth.Signup.signupPasswordCreation(this, token, password, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        signupProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            String message = data.getString("message");

                            Toast.makeText(SignupPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SignupPasswordActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SignupPasswordActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                            signupBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(SignupPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        signupBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}