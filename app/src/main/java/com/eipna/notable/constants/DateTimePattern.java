package com.eipna.notable.constants;

import androidx.annotation.NonNull;

public enum DateTimePattern {
    DETAILED_WITH_TIME("MMMM dd yyyy hh:mm a"),
    DETAILED_WITHOUT_TIME("MMMM dd, yyyy"),
    DAY_MONTH_YEAR("dd/MM/yyyy"),
    MONTH_DAY_YEAR("MM/dd/yyyy"),
    YEAR_MONTH_DAY("yyyy/MM/dd"),
    YEAR_DAY_MONTH("yyyy/dd/MM");

    private final String pattern;

    DateTimePattern(String pattern) {
        this.pattern = pattern;
    }

    @NonNull
    @Override
    public String toString() {
        return pattern;
    }
}