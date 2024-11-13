package com.eipna.notable.constants;

public enum NoteState {
    ACTIVE(1), ARCHIVED(2), DELETED(3), FAVORITE_YES(1), FAVORITE_NO(0);

    private final int state;

    NoteState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}