package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rotiking.client.R;
import com.rotiking.client.common.auth.Auth;
import com.rotiking.client.models.Address;
import com.rotiking.client.utils.Pass;

import java.util.Objects;

public class AddressRecyclerAdapter extends FirestoreRecyclerAdapter<Address, AddressRecyclerAdapter.AddressHolder> {
    private final TextView savedAddressI;
    private final Pass pass;

    public AddressRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Address> options, TextView savedAddressI, Pass pass) {
        super(options);
        this.savedAddressI = savedAddressI;
        this.pass = pass;
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressHolder holder, int position, @NonNull Address model) {
        savedAddressI.setVisibility(View.GONE);
        holder.setAddressDetails(model);

        holder.itemView.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).set(model).addOnSuccessListener(pass::on);
        });
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false));
    }

    public static class AddressHolder extends RecyclerView.ViewHolder {
        private final TextView name, address, phone;

        public AddressHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
        }

        public void setAddressDetails(Address model) {
            name.setText(model.getName());
            address.setText(model.getAddress());
            phone.setText(model.getPhone());
        }
    }
}
