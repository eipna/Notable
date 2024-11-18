package com.eipna.notable.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.eipna.notable.constants.NoteState;
import com.eipna.notable.utils.DateUtil;

import java.util.Comparator;

public class NoteModel implements Parcelable {

    private int Id;
    private String title;
    private String content;
    private long dateCreated;
    private long lastUpdated;
    private int state;
    private int isFavorite;

    public static final String EMPTY_TITLE = "Empty title";
    public static final String EMPTY_CONTENT = "Nothing to see here.";

    public NoteModel() {
        this.Id = -1;
        this.title = null;
        this.content = null;
        this.dateCreated = DateUtil.getCurrentTime();
        this.lastUpdated = DateUtil.getCurrentTime();
        this.state = NoteState.ACTIVE.getValue();
        this.isFavorite = NoteState.FAVORITE_NO.getValue();
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getTitle() {
        return title;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public static final Comparator<NoteModel> SORT_TITLE_ASCENDING = Comparator.comparing(note0 -> note0.getTitle().toLowerCase());

    public static final Comparator<NoteModel> SORT_TITLE_DESCENDING = (note01, note02) -> note02.getTitle().toLowerCase().compareTo(note01.getTitle().toLowerCase());

    public static final Comparator<NoteModel> SORT_DATE_CREATED_ASCENDING = Comparator.comparingLong(NoteModel::getDateCreated);

    public static final Comparator<NoteModel> SORT_DATE_CREATED_DESCENDING = (note01, note02) -> Long.compare(note02.getDateCreated(), note01.getDateCreated());

    public static final Comparator<NoteModel> SORT_LAST_UPDATED_ASCENDING = Comparator.comparingLong(NoteModel::getLastUpdated);

    public static final Comparator<NoteModel> SORT_LAST_UPDATED_DESCENDING = (note01, note02) -> Long.compare(note02.getLastUpdated(), note01.getLastUpdated());

    protected NoteModel(Parcel parcel) {
        this.Id = parcel.readInt();
        this.title = parcel.readString();
        this.content = parcel.readString();
        this.dateCreated = parcel.readLong();
        this.lastUpdated = parcel.readLong();
        this.state = parcel.readInt();
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
        destination.writeInt(this.Id);
        destination.writeString(this.title);
        destination.writeString(this.content);
        destination.writeLong(this.dateCreated);
        destination.writeLong(this.lastUpdated);
        destination.writeInt(this.state);
        destination.writeInt(this.isFavorite);
    }
}