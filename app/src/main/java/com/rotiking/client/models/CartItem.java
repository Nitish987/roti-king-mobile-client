package com.rotiking.client.models;

import java.io.Serializable;
import java.util.List;

public class CartItem implements Serializable {
    private Food food_data;
    private String food_id;
    private int food_price;
    private String item_id;
    private int quantity;
    private String topping_ids;
    private int topping_price;
    private List<Topping> toppings;
    private int total_price;

    public CartItem() {}

    public CartItem(Food food_data, String food_id, int food_price, String item_id, int quantity, String topping_ids, int topping_price, List<Topping> toppings, int total_price) {
        this.food_data = food_data;
        this.food_id = food_id;
        this.food_price = food_price;
        this.item_id = item_id;
        this.quantity = quantity;
        this.topping_ids = topping_ids;
        this.topping_price = topping_price;
        this.toppings = toppings;
        this.total_price = total_price;
    }

    public Food getFood_data() {
        return food_data;
    }

    public void setFood_data(Food food_data) {
        this.food_data = food_data;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTopping_ids() {
        return topping_ids;
    }

    public void setTopping_ids(String topping_ids) {
        this.topping_ids = topping_ids;
    }

    public int getTopping_price() {
        return topping_price;
    }

    public void setTopping_price(int topping_price) {
        this.topping_price = topping_price;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }
}
