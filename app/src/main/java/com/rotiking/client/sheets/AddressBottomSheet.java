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
    private EditText name, phone, address;
    private AppCompatButton saveAddressBtn;

    private String myName = null, myPhone = null, myAddress = null;

    public static AddressBottomSheet newInstance(String myName, String myPhone, String myAddress) {
        AddressBottomSheet addressBottomSheet = new AddressBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", myName);
        bundle.putString("PHONE", myPhone);
        bundle.putString("ADDRESS", myAddress);
        addressBottomSheet.setArguments(bundle);
        return addressBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myName = getArguments().getString("NAME");
            myPhone = getArguments().getString("PHONE");
            myAddress = getArguments().getString("ADDRESS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_address, container, false);

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        saveAddressBtn = view.findViewById(R.id.save_address);

        if (!myName.equals(getString(R.string.no_name))) name.setText(myName);
        if (!myPhone.equals(getString(R.string.no_phone))) phone.setText(myPhone.substring(3));
        if (!myAddress.equals(getString(R.string.no_address))) address.setText(myAddress);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        saveAddressBtn.setOnClickListener(view1 -> {
            String name_ = name.getText().toString();
            String phone_ = phone.getText().toString();
            String address_ = address.getText().toString();

            if (Validator.isEmpty(name_)) {
                name.setError("Name required.");
                return;
            }

            if (Validator.isEmpty(phone_) || !Validator.isEqualLength(phone_, 10)) {
                phone.setError("Phone required.");
                return;
            }

            if (Validator.isEmpty(address_)) {
                address.setError("Address required.");
                return;
            }

            if (phone_.length() == 12) phone_ = "+" + phone_;
            else if (phone_.length() == 10) phone_ = "+91" + phone_;

            Map<String, String> map = new HashMap<>();
            map.put("name", name_);
            map.put("phone", phone_);
            map.put("address", address_);
            FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).set(map).addOnFailureListener(e -> {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(unused -> {
                dismiss();
            });
        });
    }
}