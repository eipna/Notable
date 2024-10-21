package com.eipna.notable.data.model;

public class NoteModel {

    private int noteId;
    private String noteTitle;
    private String noteContent;
    private long noteDateCreated;
    private long noteDateEdited;

    public static final String EMPTY_NOTE = "Empty note.";

    public NoteModel() {
        this.noteId = -1;
        this.noteTitle = null;
        this.noteContent = null;
        this.noteDateCreated = -1;
        this.noteDateEdited = -1;
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

    public long getNoteDateCreated() {
        return noteDateCreated;
    }

    public void setNoteDateCreated(long noteDateCreated) {
        this.noteDateCreated = noteDateCreated;
    }

    public long getNoteDateEdited() {
        return noteDateEdited;
    }

    public void setNoteDateEdited(long noteDateEdited) {
        this.noteDateEdited = noteDateEdited;
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