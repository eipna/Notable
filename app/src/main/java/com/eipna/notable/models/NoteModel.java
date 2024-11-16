package com.eipna.notable.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.eipna.notable.enums.NoteState;
import com.eipna.notable.utils.DateUtil;

public class NoteModel implements Parcelable {

    private int noteId;
    private String noteTitle;
    private String noteContent;
    private long noteDateCreated;
    private long noteLastUpdated;
    private int noteState;
    private int isFavorite;

    public NoteModel() {
        this.noteId = -1;
        this.noteTitle = null;
        this.noteContent = null;
        this.noteDateCreated = DateUtil.getCurrentTime();
        this.noteLastUpdated = DateUtil.getCurrentTime();
        this.noteState = NoteState.ACTIVE.getValue();
        this.isFavorite = NoteState.FAVORITE_NO.getValue();
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

    public int getNoteState() {
        return noteState;
    }

    public void setNoteState(int noteState) {
        this.noteState = noteState;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    protected NoteModel(Parcel parcel) {
        this.noteId = parcel.readInt();
        this.noteTitle = parcel.readString();
        this.noteContent = parcel.readString();
        this.noteDateCreated = parcel.readLong();
        this.noteLastUpdated = parcel.readLong();
        this.noteState = parcel.readInt();
        this.isFavorite = parcel.readInt();
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel parcel) {
            return new NoteModel(parcel);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel destination, int flags) {
        destination.writeInt(this.noteId);
        destination.writeString(this.noteTitle);
        destination.writeString(this.noteContent);
        destination.writeLong(this.noteDateCreated);
        destination.writeLong(this.noteLastUpdated);
        destination.writeInt(this.noteState);
        destination.writeInt(this.isFavorite);
    }
}