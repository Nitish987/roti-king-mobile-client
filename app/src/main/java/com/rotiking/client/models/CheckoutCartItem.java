package com.rotiking.client.models;

import java.io.Serializable;

public class CheckoutCartItem implements Serializable {
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
