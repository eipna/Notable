package com.eipna.notable.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String PATTERN_DETAILED = "EEEE, dd MMMM yyyy";
    public static final String PATTERN_DD_MM_YYYY = "dd/mm/yyyy";

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getDateString(String pattern, long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(timestamp));
    }
}