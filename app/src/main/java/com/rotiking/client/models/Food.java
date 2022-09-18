package com.rotiking.client.models;

public class Food {
    private String food_id;
    private String name;
    private String photo;
    private String description;
    private String food_includes;
    private String ingredients;
    private String food_type;
    private boolean available;
    private int price;
    private int discount;
    private double rating;

    public Food() {}

    public Food(String food_id, String name, String photo, String description, String food_includes, String ingredients, String food_type, boolean available, int price, int discount, double rating) {
        this.food_id = food_id;
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.food_includes = food_includes;
        this.ingredients = ingredients;
        this.food_type = food_type;
        this.available = available;
        this.price = price;
        this.discount = discount;
        this.rating = rating;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFood_includes() {
        return food_includes;
    }

    public void setFood_includes(String food_includes) {
        this.food_includes = food_includes;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
