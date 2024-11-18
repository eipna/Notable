package com.eipna.notable.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.eipna.notable.constants.NoteState;
import com.eipna.notable.utils.DateUtil;

import java.util.Comparator;

public class NoteModel implements Parcelable {

    private int noteId;
    private String noteTitle;
    private String noteContent;
    private long noteDateCreated;
    private long noteLastUpdated;
    private int noteState;
    private int isFavorite;

    public static final String EMPTY_TITLE = "Empty title";
    public static final String EMPTY_CONTENT = "Nothing to see here.";

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

    public static final Comparator<NoteModel> SORT_TITLE_ASCENDING = Comparator.comparing(note0 -> note0.getNoteTitle().toLowerCase());

    public static final Comparator<NoteModel> SORT_TITLE_DESCENDING = (note01, note02) -> note02.getNoteTitle().toLowerCase().compareTo(note01.getNoteTitle().toLowerCase());

    public static final Comparator<NoteModel> SORT_DATE_CREATED_ASCENDING = Comparator.comparingLong(NoteModel::getNoteDateCreated);

    public static final Comparator<NoteModel> SORT_DATE_CREATED_DESCENING = (note01, note02) -> Long.compare(note02.getNoteDateCreated(), note01.getNoteDateCreated());

    public static final Comparator<NoteModel> SORT_LAST_UPDATED_ASCENDING = Comparator.comparingLong(NoteModel::getNoteLastUpdated);

    public static final Comparator<NoteModel> SORT_LAST_UPDATED_DESCENING = (note01, note02) -> Long.compare(note02.getNoteLastUpdated(), note01.getNoteLastUpdated());

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