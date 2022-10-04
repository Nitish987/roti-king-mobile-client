package com.rotiking.client.models;

import java.io.Serializable;

public class Food implements Serializable {
    private boolean available;
    private String by;
    private String description;
    private int discount;
    private String food_id;
    private String food_includes;
    private String food_type;
    private String ingredients;
    private String name;
    private String photo;
    private int price;
    private double rating;

    public Food() {}

    public Food(boolean available, String by, String description, int discount, String food_id, String food_includes, String food_type, String ingredients, String name, String photo, int price, double rating) {
        this.available = available;
        this.by = by;
        this.description = description;
        this.discount = discount;
        this.food_id = food_id;
        this.food_includes = food_includes;
        this.food_type = food_type;
        this.ingredients = ingredients;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.rating = rating;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getFood_includes() {
        return food_includes;
    }

    public void setFood_includes(String food_includes) {
        this.food_includes = food_includes;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
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
}
