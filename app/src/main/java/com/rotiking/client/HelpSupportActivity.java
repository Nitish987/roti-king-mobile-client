package com.rotiking.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class HelpSupportActivity extends AppCompatActivity {
    private EditText email, message;
    private Spinner type;
    private AppCompatButton send;
    private ImageButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        email = findViewById(R.id.email);
        type = findViewById(R.id.help_type);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        close = findViewById(R.id.close);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Auth.isUserAuthenticated(this)) {
            email.setText(Auth.getAuthUserEmail());
            email.setEnabled(false);
        }

        send.setOnClickListener(view -> {
            String email = this.email.getText().toString();
            String message = this.message.getText().toString();

            if (!Validator.isEmail(email)) {
                String e_ = "Invalid Email";
                this.email.setError(e_);
                return;
            }

            if (Validator.isEmpty(message)) {
                String e_ = "Field Required.";
                this.message.setError(e_);
                return;
            }

            DocumentReference ref = FirebaseFirestore.getInstance().collection("help").document();

            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("type", type.getSelectedItem().toString());
            map.put("message", message);
            map.put("time", System.currentTimeMillis());
            map.put("by", Auth.isUserAuthenticated(this)? Auth.getAuthUserUid() : "");
            map.put("help_id", ref.getId());

            ref.set(map).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Message sent Successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Unable to send Message.", Toast.LENGTH_SHORT).show());
        });

        close.setOnClickListener(view -> finish());
    }
}