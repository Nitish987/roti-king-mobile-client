package com.rotiking.client.models;

import java.io.Serializable;
import java.util.List;

public class CartItem implements Serializable {
    private String item_id;
    private String food_id;
    private int quantity;
    private int food_price;
    private String topping_ids;
    private int topping_price;
    private int total_price;
    private Food food_data;
    private List<Topping> toppings;

    public CartItem(String item_id, String food_id, int quantity, int food_price, String topping_ids, int topping_price, int total_price, Food food_data, List<Topping> toppings) {
        this.item_id = item_id;
        this.food_id = food_id;
        this.quantity = quantity;
        this.food_price = food_price;
        this.topping_ids = topping_ids;
        this.topping_price = topping_price;
        this.total_price = total_price;
        this.food_data = food_data;
        this.toppings = toppings;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
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

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public Food getFood_data() {
        return food_data;
    }

    public void setFood_data(Food food_data) {
        this.food_data = food_data;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }
}
