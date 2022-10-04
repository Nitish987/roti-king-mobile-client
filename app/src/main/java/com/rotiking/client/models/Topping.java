package com.rotiking.client.models;

import java.io.Serializable;

public class Topping implements Serializable {
    private boolean available;
    private String by;
    private String food_includes;
    private String name;
    private String photo;
    private int price;
    private double rating;
    private String topping_id;

    public Topping() {}

    public Topping(boolean available, String by, String food_includes, String name, String photo, int price, double rating, String topping_id) {
        this.available = available;
        this.by = by;
        this.food_includes = food_includes;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.rating = rating;
        this.topping_id = topping_id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getFood_includes() {
        return food_includes;
    }

    public void setFood_includes(String food_includes) {
        this.food_includes = food_includes;
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

    public String getTopping_id() {
        return topping_id;
    }

    public void setTopping_id(String topping_id) {
        this.topping_id = topping_id;
    }
}
