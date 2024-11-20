package com.eipna.notable.constants;

public enum AppTheme {
    DARK_MODE("dark"),
    LIGHT_MODE("light"),
    SYSTEM_MODE("system"),
    BATTERY_MODE("battery");

    private final String value;

    AppTheme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}