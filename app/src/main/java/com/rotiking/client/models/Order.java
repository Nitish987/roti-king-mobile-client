package com.rotiking.client.models;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String address;
    private String agentName;
    private String agentPhone;
    private String agentUid;
    private List<CartItem> items;
    private int deliveryPrice;
    private int discount;
    private GeoPoint location;
    private String name;
    private String orderId;
    private int orderNumber;
    private int orderState;
    private boolean orderSuccess;
    private int payablePrice;
    private String paymentMethod;
    private String paymentOrderID;
    private String phone;
    private String secureNumber;
    private long time;
    private int totalPrice;
    private String uid;

    public Order() {}

    public Order(String address, String agentName, String agentPhone, String agentUid, List<CartItem> items, int deliveryPrice, int discount, GeoPoint location, String name, String orderId, int orderNumber, int orderState, boolean orderSuccess, int payablePrice, String paymentMethod, String paymentOrderID, String phone, String secureNumber, long time, int totalPrice, String uid) {
        this.address = address;
        this.agentName = agentName;
        this.agentPhone = agentPhone;
        this.agentUid = agentUid;
        this.items = items;
        this.deliveryPrice = deliveryPrice;
        this.discount = discount;
        this.location = location;
        this.name = name;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderState = orderState;
        this.orderSuccess = orderSuccess;
        this.payablePrice = payablePrice;
        this.paymentMethod = paymentMethod;
        this.paymentOrderID = paymentOrderID;
        this.phone = phone;
        this.secureNumber = secureNumber;
        this.time = time;
        this.totalPrice = totalPrice;
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(int deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public boolean isOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(boolean orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public int getPayablePrice() {
        return payablePrice;
    }

    public void setPayablePrice(int payablePrice) {
        this.payablePrice = payablePrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getSecureNumber() {
        return secureNumber;
    }

    public void setSecureNumber(String secureNumber) {
        this.secureNumber = secureNumber;
    }

    public String getAgentUid() {
        return agentUid;
    }

    public void setAgentUid(String agentUid) {
        this.agentUid = agentUid;
    }

    public String getPaymentOrderID() {
        return paymentOrderID;
    }

    public void setPaymentOrderID(String paymentOrderID) {
        this.paymentOrderID = paymentOrderID;
    }
}
