package com.eipna.notable.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    public static final String PATTERN_DETAILED_TIME = "MMMM dd yyyy hh:mm a";

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getDateString(String pattern, long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(timestamp));
    }
}