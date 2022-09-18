package com.rotiking.client.models;

public class CheckoutCartItem {
    private String OrderName;
    private int OrderPrice;

    public CheckoutCartItem(String orderName, int orderPrice) {
        OrderName = orderName;
        OrderPrice = orderPrice;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public int getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        OrderPrice = orderPrice;
    }
}
