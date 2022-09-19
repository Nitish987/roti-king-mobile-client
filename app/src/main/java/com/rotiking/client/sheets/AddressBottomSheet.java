package com.rotiking.client.sheets;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rotiking.client.R;
import com.rotiking.client.utils.Validator;

public class AddressBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private EditText address;
    private AppCompatButton saveAddressBtn;

    public static AddressBottomSheet newInstance() {
        return new AddressBottomSheet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_address, container, false);

        address = view.findViewById(R.id.address);
        saveAddressBtn = view.findViewById(R.id.save_address);

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


        });
    }
}