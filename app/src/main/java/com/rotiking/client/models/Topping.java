package com.rotiking.client.models;

import java.io.Serializable;

public class Topping implements Serializable {
    private String topping_id;
    private String name;
    private String photo;
    private String food_includes;
    private boolean available;
    private int price;
    private double rating;

    public Topping() {}

    public Topping(String topping_id, String name, String photo, String food_includes, boolean available, int price, double rating) {
        this.topping_id = topping_id;
        this.name = name;
        this.photo = photo;
        this.food_includes = food_includes;
        this.available = available;
        this.price = price;
        this.rating = rating;
    }

    public String getTopping_id() {
        return topping_id;
    }

    public void setTopping_id(String topping_id) {
        this.topping_id = topping_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFood_includes() {
        return food_includes;
    }

    public void setFood_includes(String food_includes) {
        this.food_includes = food_includes;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
