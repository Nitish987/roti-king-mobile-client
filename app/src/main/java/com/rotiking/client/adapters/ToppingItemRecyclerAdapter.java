package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rotiking.client.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ToppingItemRecyclerAdapter extends RecyclerView.Adapter<ToppingItemRecyclerAdapter.ToppingsItemHolder> {
    private final JSONArray toppings;

    public ToppingItemRecyclerAdapter(JSONArray toppings) {
        this.toppings = toppings;
    }

    @NonNull
    @Override
    public ToppingsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToppingsItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topping, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingsItemHolder holder, int position) {
        try {
            JSONObject topping = toppings.getJSONObject(position);
            holder.setName(topping.getString(("name")));
            holder.setPhoto(topping.getString("photo"));
            holder.setDiscount(topping.getInt(("discount")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return toppings.length();
    }

    public static class ToppingsItemHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView name, discount;

        public ToppingsItemHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.photo);
            discount = itemView.findViewById(R.id.discount);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setPhoto(String photo) {
            if (!photo.equals("")) Glide.with(this.photo.getContext()).load(photo).into(this.photo);
        }

        public void setDiscount(int discount) {
            if (discount == 0) {
                this.discount.setVisibility(View.INVISIBLE);
            } else {
                this.discount.setVisibility(View.VISIBLE);
                String discount_ = discount + "% OFF" ;
                this.discount.setText(discount_);
            }
        }
    }
}
