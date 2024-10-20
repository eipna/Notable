package com.eipna.notable.data.model;

public class NoteModel {

    private int noteId;
    private String noteTitle;
    private String noteContent;

    public NoteModel() {
        this.noteId = -1;
        this.noteTitle = null;
        this.noteContent = null;
    }

    public NoteModel(int noteId, String noteTitle, String noteContent) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}