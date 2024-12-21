package com.eipna.notable.interfaces;

public interface NoteListener {
    void onNoteClick(int position); // Handles single clicks for notes
    void onNoteLongClick(int position); // Handles long clicks for notes
}