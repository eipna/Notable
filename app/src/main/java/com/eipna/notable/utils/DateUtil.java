package com.eipna.notable.utils;

import android.annotation.SuppressLint;

import com.eipna.notable.enums.DateTimePattern;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getDateString(DateTimePattern pattern, long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern.toString());
        return dateFormat.format(new Date(timestamp));
    }
}