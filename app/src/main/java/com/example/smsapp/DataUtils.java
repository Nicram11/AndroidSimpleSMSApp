package com.example.smsapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataUtils {
    public static String formatDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date(Long.parseLong(date)));
    }
}
