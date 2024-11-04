package com.eipna.notable.data.model;

import com.eipna.notable.util.DateUtil;

public class NoteModel {

    private int noteId;
    private String noteTitle;
    private String noteContent;
    private long noteDateCreated;
    private long noteLastUpdated;
    private int noteStatus;
    private int isFavorite;

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_ARCHIVED = 2;
    public static final int STATUS_DELETED = 3;

    public static final int IS_FAVORITE = 1;
    public static final int NOT_FAVORITE = 0;

    public static final String EMPTY_TITLE = String.format("Note %s", DateUtil.getDateString(DateUtil.PATTERN_DETAILED_TIME, DateUtil.getCurrentTime()));
    public static final String EMPTY_NOTE = "Empty note.";

    public NoteModel() {
        this.noteId = -1;
        this.noteTitle = null;
        this.noteContent = null;
        this.noteDateCreated = -1;
        this.noteLastUpdated = -1;
        this.noteStatus = -1;
        this.isFavorite = -1;
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

    public long getNoteLastUpdated() {
        return noteLastUpdated;
    }

    public void setNoteLastUpdated(long noteLastUpdated) {
        this.noteLastUpdated = noteLastUpdated;
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

    public int getNoteStatus() {
        return noteStatus;
    }

    public void setNoteStatus(int noteStatus) {
        this.noteStatus = noteStatus;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}