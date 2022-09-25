package com.rotiking.client.utils;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class DateParser {
    public static String parse(Date date) {
        String d_ = date.toString().substring(0, 9);
        SimpleDateFormat formatTime = new SimpleDateFormat("hh.mm aa");
        return d_ + " - " + formatTime.format(date);
    }
}
