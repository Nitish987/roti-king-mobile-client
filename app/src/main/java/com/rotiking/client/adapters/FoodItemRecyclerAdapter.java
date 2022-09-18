package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rotiking.client.R;
import com.rotiking.client.models.Food;
import com.rotiking.client.sheets.FoodDetailBottomSheet;

import java.util.List;

public class FoodItemRecyclerAdapter extends RecyclerView.Adapter<FoodItemRecyclerAdapter.FoodItemHolder> {
    private final List<Food> foods;
    private final FragmentManager fragmentManager;

    public FoodItemRecyclerAdapter(List<Food> foods, FragmentManager fragmentManager) {
        this.foods = foods;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public FoodItemRecyclerAdapter.FoodItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodItemRecyclerAdapter.FoodItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemRecyclerAdapter.FoodItemHolder holder, int position) {
        Food food = foods.get(position);
        holder.setPhoto(food.getPhoto());
        holder.setName(food.getName());
        holder.setType(food.getFood_type());
        holder.setPrice(food.getPrice(), food.getDiscount());
        holder.setRating(food.getRating());

        holder.itemView.setOnClickListener(view -> FoodDetailBottomSheet.newInstance(food).show(fragmentManager, "FOOD_DETAIL_DIALOG"));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class FoodItemHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView name, type, price, rating, discount;

        public FoodItemHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.food_type);
            price = itemView.findViewById(R.id.price);
            rating = itemView.findViewById(R.id.rating);
            discount = itemView.findViewById(R.id.discount);
        }

        public void setPhoto(String photo) {
            if (!photo.equals("")) Glide.with(this.photo.getContext()).load(photo).into(this.photo);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setType(String type) {
            this.type.setText(type);
        }

        public void setPrice(int price, int discount) {
            if (discount == 0) {
                this.discount.setVisibility(View.INVISIBLE);
            } else {
                this.discount.setVisibility(View.VISIBLE);
                String discount_ = discount + "% OFF";
                this.discount.setText(discount_);
            }

            String price_ = "\u20B9 " + price;
            this.price.setText(price_);
        }

        public void setRating(double rating) {
            String rate = Double.toString(rating);
            this.rating.setText(rate);
        }
    }
}
