package com.rotiking.client.models;

import java.io.Serializable;

public class Address implements Serializable {
    private String address;
    private String name;
    private String phone;

    private Address() {}

    public Address(String address, String name, String phone) {
        this.address = address;
        this.name = name;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
