package com.rotiking.client.utils;

public interface Promise {
    void resolving(int progress, String msg);
    void resolved(Object o);
    void reject(String err);
}
