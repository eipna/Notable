package com.eipna.notable.constants;

public enum NoteSort {
    TITLE_ASCENDING(1),
    TITLE_DESCENDING(2),
    DATE_CREATED_ASCENDING(3),
    DATE_CREATED_DESCENDING(4),
    LAST_UPDATED_ASCENDING(5),
    LAST_UPDATED_DESCENDING(6);

    private final int value;

    NoteSort(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}