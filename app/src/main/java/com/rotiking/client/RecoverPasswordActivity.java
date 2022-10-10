package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;

import org.json.JSONObject;

public class RecoverPasswordActivity extends AppCompatActivity {
    private TextView titleTxt, messageTxt, resentOtp;
    private LinearLayout emailSection, otpSection, passwordSection, confirmPasswordSection;
    private EditText email_eTxt, otp_eTxt, password_eTxt, confirmPassword_eTxt;
    private AppCompatButton recoverBtn;
    private CircularProgressIndicator recoverProgress;

    private static String title = null;
    private static String message = null;
    private static String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        titleTxt = findViewById(R.id.title_txt);
        messageTxt = findViewById(R.id.message_txt);
        emailSection = findViewById(R.id.email_section);
        otpSection = findViewById(R.id.otp_section);
        passwordSection = findViewById(R.id.password_section);
        confirmPasswordSection = findViewById(R.id.confirm_password_section);
        email_eTxt = findViewById(R.id.email_e_txt);
        otp_eTxt = findViewById(R.id.otp_e_txt);
        password_eTxt = findViewById(R.id.password_e_txt);
        confirmPassword_eTxt = findViewById(R.id.confirm_password_e_txt);
        recoverBtn = findViewById(R.id.recovery_btn);
        recoverProgress = findViewById(R.id.recovery_progress);
        resentOtp = findViewById(R.id.resent_otp);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        recoverBtn.setOnClickListener(view -> {
            if (recoverBtn.getTag().equals("email")) {
                String email = email_eTxt.getText().toString();

                if (Validator.isEmpty(email)) {
                    email_eTxt.setError("Required Field");
                    return;
                }

                if (!Validator.isEmail(email)) {
                    email_eTxt.setError("Invalid Email");
                    return;
                }

                recoverBtn.setVisibility(View.INVISIBLE);

                Auth.Recovery.recoverAccount(this, email, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        recoverProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            message = data.getString("message");
                            token = data.getString("token");

                            title = "Recovery OTP";
                            titleTxt.setText(title);
                            messageTxt.setText(message);

                            emailSection.setVisibility(View.GONE);
                            otpSection.setVisibility(View.VISIBLE);
                            recoverProgress.setVisibility(View.INVISIBLE);
                            recoverBtn.setVisibility(View.VISIBLE);
                            resentOtp.setVisibility(View.VISIBLE);

                            recoverBtn.setTag("otp");

                            String btnText = "Verify";
                            recoverBtn.setText(btnText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RecoverPasswordActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                            recoverBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(RecoverPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        recoverBtn.setVisibility(View.VISIBLE);
                    }
                });

            } else if (recoverBtn.getTag().equals("otp")) {
                String otp = otp_eTxt.getText().toString();

                if (Validator.isEmpty(otp)) {
                    otp_eTxt.setError("Required Field");
                    return;
                }

                if (!Validator.isEqualLength(otp, 6)) {
                    otp_eTxt.setError("Invalid OTP.");
                    return;
                }

                recoverBtn.setVisibility(View.INVISIBLE);

                Auth.Recovery.recoverAccountOtpVerification(this, token, otp, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        recoverProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            message = data.getString("message");
                            token = data.getString("token");

                            title = "New Password";
                            titleTxt.setText(title);
                            messageTxt.setText(message);

                            otpSection.setVisibility(View.GONE);
                            passwordSection.setVisibility(View.VISIBLE);
                            confirmPasswordSection.setVisibility(View.VISIBLE);
                            recoverProgress.setVisibility(View.INVISIBLE);
                            recoverBtn.setVisibility(View.VISIBLE);
                            resentOtp.setVisibility(View.GONE);

                            recoverBtn.setTag("password");

                            String btnText = "Recover Password";
                            recoverBtn.setText(btnText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RecoverPasswordActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                            recoverBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(RecoverPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        recoverBtn.setVisibility(View.VISIBLE);
                    }
                });
            } else if (recoverBtn.getTag().equals("password")) {
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

                recoverBtn.setVisibility(View.INVISIBLE);

                Auth.Recovery.recoveryPasswordCreation(this, token, password, new Promise<JSONObject>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        recoverProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(JSONObject data) {
                        try {
                            String message = data.getString("message");
                            Toast.makeText(RecoverPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RecoverPasswordActivity.this, "something went wrong.", Toast.LENGTH_SHORT).show();
                            recoverBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(RecoverPasswordActivity.this, err, Toast.LENGTH_SHORT).show();
                        recoverBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        resentOtp.setOnClickListener(view -> {
            Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show();
            Auth.Recovery.resentOtpVerification(this, token, new Promise<String>() {
                @Override
                public void resolving(int progress, String msg) {}

                @Override
                public void resolved(String newToken) {
                    token = newToken;
                }

                @Override
                public void reject(String err) {}
            });
        });
    }
}