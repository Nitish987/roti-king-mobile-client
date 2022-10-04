package com.rotiking.client.utils;

import android.icu.text.SimpleDateFormat;

import java.text.DateFormat;
import java.util.Date;

public class DateParser {
    public static String parse(Date date) {
        String d_ = DateFormat.getDateInstance().format(date);
        SimpleDateFormat formatTime = new SimpleDateFormat("hh.mm aa");
        return d_ + " - " + formatTime.format(date);
    }
}
