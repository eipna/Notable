package com.eipna.notable.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    public static final String PATTERN_DETAILED = "EEEE, dd MMMM yyyy";
    public static final String PATTERN_DETAILED_TIME = "EEEE, dd MMMM yyyy hh:mm a";

    public static final String PATTERN_DAY_MONTH_YEAR = "dd/mm/yyyy";
    public static final String PATTERN_MONTH_DAY_YEAR = "mm/dd/yyyy";

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getDateString(String pattern, long timestamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(timestamp));
    }
}