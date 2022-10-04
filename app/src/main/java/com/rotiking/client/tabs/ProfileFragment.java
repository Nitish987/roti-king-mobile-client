package com.rotiking.client.tabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.HelpSupportActivity;
import com.rotiking.client.MyPhotoActivity;
import com.rotiking.client.R;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.common.auth.AuthPreferences;
import com.rotiking.client.sheets.AddressBottomSheet;
import com.rotiking.client.utils.Promise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private View view;
    private ImageView myPhoto;
    private TextView myNameTxt, emailTxt, usernameTxt, nameTxt, addressTxt, phoneTxt;
    private LinearLayout deliveryAddressDesk;
    private AppCompatButton logoutBtn, changePhotoBtn, helpBtn;

    private String name = null, phone = null, address = null;
    private String photo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        myPhoto = view.findViewById(R.id.photo);
        myNameTxt = view.findViewById(R.id.my_name);
        emailTxt = view.findViewById(R.id.email);
        usernameTxt = view.findViewById(R.id.username);
        nameTxt = view.findViewById(R.id.name);
        phoneTxt = view.findViewById(R.id.phone);
        addressTxt = view.findViewById(R.id.address);
        deliveryAddressDesk = view.findViewById(R.id.delivery_address_desk);
        changePhotoBtn = view.findViewById(R.id.edit_photo);
        logoutBtn = view.findViewById(R.id.logout);
        helpBtn = view.findViewById(R.id.help);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                name = value.get("name", String.class);
                phone = value.get("phone", String.class);
                address = value.get("address", String.class);

                nameTxt.setText(name);
                phoneTxt.setText(phone);
                addressTxt.setText(address);
            }
        });

        Auth.Account.profile(view.getContext(), new Promise<JSONObject>() {
            @Override
            public void resolving(int progress, String msg) {}

            @Override
            public void resolved(JSONObject data) {
                try {
                    JSONObject profile = data.getJSONObject("profile");
                    String name = profile.getString("name");
                    String email = profile.getString("email");
                    String username = profile.getString("username");

                    myNameTxt.setText(name);
                    emailTxt.setText(email);
                    usernameTxt.setText(username);

                    photo = profile.getString("photo");
                    if (!photo.equals("None")) {
                        Glide.with(view.getContext()).load(photo).into(myPhoto);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void reject(String err) {
                Toast.makeText(view.getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        changePhotoBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), MyPhotoActivity.class);
            intent.putExtra("PHOTO", photo);
            startActivity(intent);
        });

        deliveryAddressDesk.setOnClickListener(view1 -> AddressBottomSheet.newInstance(
                nameTxt.getText().toString(),
                phoneTxt.getText().toString(),
                addressTxt.getText().toString()
        ).show(getParentFragmentManager(), "DIALOG"));

        logoutBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setTitle("Logout");
            alert.setMessage("Are you sure, you want to logout.");
            alert.setCancelable(true);
            alert.setPositiveButton("Yes", (dialogInterface, i) -> {
                AuthPreferences preferences = new AuthPreferences(view.getContext());
                preferences.clear();
                FirebaseAuth.getInstance().signOut();
            });
            alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });

        helpBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), HelpSupportActivity.class);
            startActivity(intent);
        });
    }
}