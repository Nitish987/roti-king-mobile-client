package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rotiking.client.R;
import com.rotiking.client.models.CheckoutCartItem;

import java.util.List;

public class CheckoutCartItemRecyclerAdapter extends RecyclerView.Adapter<CheckoutCartItemRecyclerAdapter.CheckoutCartItemHolder> {
    private final List<CheckoutCartItem> checkoutCartItems;

    public CheckoutCartItemRecyclerAdapter(List<CheckoutCartItem> checkoutCartItems) {
        this.checkoutCartItems = checkoutCartItems;
    }

    @NonNull
    @Override
    public CheckoutCartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckoutCartItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_checkout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutCartItemHolder holder, int position) {
        CheckoutCartItem item = checkoutCartItems.get(position);
        holder.setOrder(item);
    }

    @Override
    public int getItemCount() {
        return checkoutCartItems.size();
    }

    public static class CheckoutCartItemHolder extends RecyclerView.ViewHolder {
        private final TextView orderName, price;

        public CheckoutCartItemHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.order_name);
            price = itemView.findViewById(R.id.price);
        }

        private void setOrder(CheckoutCartItem item) {
            orderName.setText(item.getOrderName());
            String pri_ = "\u20B9 " + item.getOrderPrice();
            price.setText(pri_);
        }
    }
}
