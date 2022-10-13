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
import com.google.firebase.auth.GoogleAuthProvider;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.auth.AuthPreferences;
import com.rotiking.client.common.db.Database;
import com.rotiking.client.common.security.AES128;
import com.rotiking.client.utils.Promise;
import com.rotiking.client.utils.Validator;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton loginBtn;
    private TextView signupTxt, forgetPasswordTxt;
    private EditText email_eTxt, password_eTxt;
    private CircularProgressIndicator loginProgress;
    private SignInButton googleSignBtn;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleResultIntent;

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
        googleSignBtn = findViewById(R.id.google_sign_in_btn);

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
        signupTxt.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

        password_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password_eTxt.getText().toString().equals("")) {
                    googleSignBtn.setVisibility(View.VISIBLE);
                } else {
                    googleSignBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
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
                startLoginProcess(authResult.getUser().getUid());
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

        googleResultIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
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
                loginBtn.setVisibility(View.INVISIBLE);
                googleSignBtn.setVisibility(View.INVISIBLE);
                loginProgress.setVisibility(View.VISIBLE);
                startLoginProcess(task.getResult().getUser().getUid());
            } else {
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLoginProcess(String uid) {
        Database.getInstance().collection("user").document(uid).collection("data").document("profile").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (!documentSnapshot.get("accType", String.class).equals("CLIENT")) {
                    Toast.makeText(LoginActivity.this, "You have no Permission to login to this app.", Toast.LENGTH_SHORT).show();
                    Auth.getInstance().signOut();
                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                    return;
                }

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
                googleSignBtn.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "No account exists. Signup First.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            googleSignBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            loginProgress.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
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