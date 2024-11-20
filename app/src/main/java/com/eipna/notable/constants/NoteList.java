package com.eipna.notable.constants;

public enum NoteList {
    LIST("list"),
    GRID("grid");

    private final String value;

    NoteList(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}