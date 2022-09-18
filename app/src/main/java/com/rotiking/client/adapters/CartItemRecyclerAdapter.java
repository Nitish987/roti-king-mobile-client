package com.rotiking.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rotiking.client.R;
import com.rotiking.client.models.CartItem;
import com.rotiking.client.models.Food;
import com.rotiking.client.models.Topping;
import com.rotiking.client.utils.Pass;

import java.util.HashSet;
import java.util.List;

public class CartItemRecyclerAdapter extends RecyclerView.Adapter<CartItemRecyclerAdapter.CartItemHolder> {
    private final List<CartItem> cartItems;
    private int totalCartPrice;
    private final Pass pass;
    public static HashSet<String> REMOVED_CART_ITEM;

    public CartItemRecyclerAdapter(List<CartItem> cartItems, int totalCartPrice, Pass pass) {
        this.cartItems = cartItems;
        this.totalCartPrice = totalCartPrice;
        this.pass = pass;
        REMOVED_CART_ITEM = new HashSet<>();
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        CartItem item = cartItems.get(position);
        String itemId = item.getItem_id();

        Food food = item.getFood_data();
        holder.setPhoto(food.getPhoto());
        holder.setName(food.getName());
        holder.setType(food.getFood_type());
        holder.setDiscount(food.getDiscount());
        holder.setRating(food.getRating());
        holder.setQuantity(item.getQuantity());
        holder.setToppings(item.getTopping_ids(), item.getToppings());

        int price = item.getTotal_price();
        holder.setPrice(price);

        if (REMOVED_CART_ITEM.contains(itemId)) {
            holder.cartItemLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.red_transparent));
            holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            holder.cartItemLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.transparent));
            holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_close_24);
        }

        holder.removeCartItemBtn.setOnClickListener(view -> {
            if (REMOVED_CART_ITEM.contains(itemId)) {
                holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_check_24);
                REMOVED_CART_ITEM.remove(itemId);
                totalCartPrice += price;
            } else {
                holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_close_24);
                REMOVED_CART_ITEM.add(itemId);
                totalCartPrice -= price;
            }
            pass.on(totalCartPrice);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartItemHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView name, type, price, rating, discount, quantity, toppings;
        private final AppCompatImageButton removeCartItemBtn;
        private final LinearLayout cartItemLayout;

        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.food_type);
            price = itemView.findViewById(R.id.price);
            rating = itemView.findViewById(R.id.rating);
            discount = itemView.findViewById(R.id.discount);
            quantity = itemView.findViewById(R.id.quantity);
            toppings = itemView.findViewById(R.id.toppings_added);
            removeCartItemBtn = itemView.findViewById(R.id.remove_cart_item);
            cartItemLayout = itemView.findViewById(R.id.cart_item_layout);
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

        public void setDiscount(int discount) {
            if (discount == 0) {
                this.discount.setVisibility(View.INVISIBLE);
            } else {
                this.discount.setVisibility(View.VISIBLE);
                String discount_ = discount + "% OFF";
                this.discount.setText(discount_);
            }
        }

        public void setRating(double rating) {
            String rate = Double.toString(rating);
            this.rating.setText(rate);
        }

        public void setQuantity(int quantity) {
            String qua_ = "Quantity : " + quantity;
            this.quantity.setText(qua_);
        }

        public void setToppings(String toppings_ids, List<Topping> toppings) {
            if (toppings_ids.equals("None")) {
                this.toppings.setVisibility(View.GONE);
            } else {
                this.toppings.setVisibility(View.VISIBLE);

                StringBuilder top_ = new StringBuilder();
                top_.append("Toppings Added ").append("(").append(toppings.size()).append(") : ");
                for (int i = 0; i < toppings.size(); i++) {
                    Topping topping = toppings.get(i);
                    top_.append(" ").append(topping.getName()).append(",");
                }
                top_.replace(top_.length() - 1, top_.length(), "");
                this.toppings.setText(top_);
            }
        }

        private void setPrice(int totalPrice) {
            String pri_ = "\u20B9 " + totalPrice;
            this.price.setText(pri_);
        }
    }
}
