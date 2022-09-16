package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rotiking.client.R;
import com.rotiking.client.utils.Pass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class ToppingItemRecyclerAdapter extends RecyclerView.Adapter<ToppingItemRecyclerAdapter.ToppingsItemHolder> {
    public static HashSet<String> toppingIds;
    private final JSONArray toppings;
    private final Pass pass;
    private static int TOTAL_TOPPINGS_PRICE = 0;

    public ToppingItemRecyclerAdapter(JSONArray toppings, Pass pass) {
        this.toppings = toppings;
        this.pass = pass;
        toppingIds = new HashSet<>();
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

            int price = topping.getInt("price");
            holder.setPrice(price);

            String toppingId = topping.getString("topping_id");
            if (toppingIds.contains(toppingId)) {
                holder.toppingLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.green_transparent));
            } else {
                holder.toppingLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.transparent));
            }

            holder.itemView.setOnClickListener(view -> {
                if (toppingIds.contains(toppingId)) {
                    toppingIds.remove(toppingId);
                    TOTAL_TOPPINGS_PRICE -= price;
                } else {
                    toppingIds.add(toppingId);
                    TOTAL_TOPPINGS_PRICE += price;
                }
                pass.on(TOTAL_TOPPINGS_PRICE);
                notifyItemChanged(position);
            });
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
        private final TextView name, price;
        public final LinearLayout toppingLayout;

        public ToppingsItemHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.photo);
            price = itemView.findViewById(R.id.price);
            toppingLayout = itemView.findViewById(R.id.topping_layout);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setPhoto(String photo) {
            if (!photo.equals("")) Glide.with(this.photo.getContext()).load(photo).into(this.photo);
        }

        public void setPrice(int price) {
            String price_= "\u20B9 " + price;
            this.price.setText(price_);
        }
    }
}
