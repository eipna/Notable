package com.eipna.notable.constants;

import androidx.annotation.NonNull;

public enum NoteState {
    ACTIVE(1, "Active"),
    ARCHIVED(2, "Archived"),
    DELETED(3, "Deleted"),
    FAVORITE_YES(1, "Favorite"),
    FAVORITE_NO(0, "Not Favorite");

    private final int value;
    private final String string;

    NoteState(int value, String string) {
        this.value = value;
        this.string = string;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return string;
    }
}