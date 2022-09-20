package com.rotiking.client.sheets;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.R;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.utils.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddressBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private EditText address;
    private AppCompatButton saveAddressBtn;

    private String myAddress = null;

    public static AddressBottomSheet newInstance(String myAddress) {
        AddressBottomSheet addressBottomSheet = new AddressBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("ADDRESS", myAddress);
        addressBottomSheet.setArguments(bundle);
        return addressBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myAddress = getArguments().getString("ADDRESS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_address, container, false);

        address = view.findViewById(R.id.address);
        saveAddressBtn = view.findViewById(R.id.save_address);

        address.setText(myAddress);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        saveAddressBtn.setOnClickListener(view1 -> {
            String address_ = address.getText().toString();

            if (Validator.isEmpty(address_)) {
                address.setError("Address required.");
                return;
            }

            Map<String, String> map = new HashMap<>();
            map.put("address", address_);
            FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).set(map).addOnFailureListener(e -> {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(unused -> {
                dismiss();
            });
        });
    }
}