package com.rotiking.client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private AppCompatButton continueBtn;
    private TextView loginTxt;
    private EditText name_eTxt, email_eTxt;
    private ProgressBar continueProgress;
    private SignInButton googleSignBtn;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        continueBtn = findViewById(R.id.continue_btn);
        loginTxt = findViewById(R.id.login_txt);
        name_eTxt = findViewById(R.id.name_e_txt);
        email_eTxt = findViewById(R.id.email_e_txt);
        continueProgress = findViewById(R.id.continue_progress);
        googleSignBtn = findViewById(R.id.google_sign_btn);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        name_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (name_eTxt.getText().toString().equals("")) {
                    googleSignBtn.setVisibility(View.VISIBLE);
                } else {
                    googleSignBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (email_eTxt.getText().toString().equals("")) {
                    googleSignBtn.setVisibility(View.VISIBLE);
                } else {
                    googleSignBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
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
                    continueBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            });
        });

        googleResultIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                googleSignBtn.setVisibility(View.GONE);
                continueBtn.setVisibility(View.GONE);
                continueProgress.setVisibility(View.VISIBLE);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Signup failed!", Toast.LENGTH_LONG).show();
            }
        });
        
        googleSignBtn.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleResultIntent.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                Database.getInstance().collection("user_exists").document(user.getEmail()).get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        continueProgress.setVisibility(View.INVISIBLE);
                        googleSignBtn.setVisibility(View.VISIBLE);
                        continueBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Account already exists with this email. Try again with another email.", Toast.LENGTH_SHORT).show();
                        Auth.getInstance().signOut();
                    } else {
                        Database.getInstance().collection("app").document("account").get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()){
                                Auth.Signup.googleSignupCreation(
                                        this,
                                        documentSnapshot.get("googleSignInKey", String.class),
                                        user.getUid(),
                                        user.getDisplayName(),
                                        user.getEmail(),
                                        new Promise<String>() {
                                            @Override
                                            public void resolving(int progress, String msg) {
                                                continueProgress.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void resolved(String msg) {
                                                continueProgress.setVisibility(View.INVISIBLE);
                                                Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                Auth.getInstance().signOut();
                                                finish();
                                            }

                                            @Override
                                            public void reject(String err) {
                                                continueProgress.setVisibility(View.INVISIBLE);
                                                googleSignBtn.setVisibility(View.VISIBLE);
                                                continueBtn.setVisibility(View.VISIBLE);

                                                Toast.makeText(SignupActivity.this, err, Toast.LENGTH_SHORT).show();
                                                Auth.getInstance().signOut();
                                            }
                                        }
                                );
                            }
                        }).addOnFailureListener(e -> {
                            continueProgress.setVisibility(View.INVISIBLE);
                            googleSignBtn.setVisibility(View.VISIBLE);
                            continueBtn.setVisibility(View.VISIBLE);

                            Toast.makeText(this, "Signup failed!", Toast.LENGTH_SHORT).show();
                            Auth.getInstance().signOut();
                        });
                    }
                }).addOnFailureListener(e -> {
                    continueProgress.setVisibility(View.INVISIBLE);
                    googleSignBtn.setVisibility(View.VISIBLE);
                    continueBtn.setVisibility(View.VISIBLE);

                    Toast.makeText(this, "Signup failed!", Toast.LENGTH_SHORT).show();
                    Auth.getInstance().signOut();
                });
            } else {
                continueProgress.setVisibility(View.INVISIBLE);
                googleSignBtn.setVisibility(View.VISIBLE);
                continueBtn.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Signup failed!", Toast.LENGTH_SHORT).show();
            }
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