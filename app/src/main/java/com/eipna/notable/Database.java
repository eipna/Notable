package com.eipna.notable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.eipna.notable.constants.NoteState;
import com.eipna.notable.models.NoteModel;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    // Database credentials
    private static final String DATABASE_NAME = "notable.db";
    private static final int DATABASE_VERSION = 1;

    // Note table and columns
    public static final String TABLE_NOTE = "notes";
    public static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_DATE_CREATED = "date_created";
    public static final String COLUMN_NOTE_LAST_UPDATED = "last_updated";
    public static final String COLUMN_NOTE_STATE = "state";
    public static final String COLUMN_NOTE_FAVORITE = "is_favorite";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String noteQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTE + "(" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT, " +
                COLUMN_NOTE_CONTENT + " TEXT, " +
                COLUMN_NOTE_DATE_CREATED + " LONG, " +
                COLUMN_NOTE_LAST_UPDATED + " LONG, " +
                COLUMN_NOTE_STATE + " INTEGER, " +
                COLUMN_NOTE_FAVORITE + " INTEGER)";
        sqLiteDatabase.execSQL(noteQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(sqLiteDatabase);
    }

    public void createNote(NoteModel createdNote) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, createdNote.getTitle());
        values.put(COLUMN_NOTE_CONTENT, createdNote.getContent());
        values.put(COLUMN_NOTE_DATE_CREATED, createdNote.getDateCreated());
        values.put(COLUMN_NOTE_LAST_UPDATED, createdNote.getLastUpdated());
        values.put(COLUMN_NOTE_STATE, createdNote.getState());
        values.put(COLUMN_NOTE_FAVORITE, createdNote.getIsFavorite());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> getActiveNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> notes = new ArrayList<>();
        String readNotesQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STATE + " = ?";

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readNotesQuery, new String[]{String.valueOf(NoteState.ACTIVE.getValue())});
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                notes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> getArchivedNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> notes = new ArrayList<>();
        String readNotesQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STATE + " = ?";

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readNotesQuery, new String[]{String.valueOf(NoteState.ARCHIVED.getValue())});
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                notes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> getDeletedNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> notes = new ArrayList<>();
        String readNotesQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STATE + " = ?";

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readNotesQuery, new String[]{String.valueOf(NoteState.DELETED.getValue())});
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                notes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> getFavoriteNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> favoriteNotes = new ArrayList<>();
        String readFavoriteNotes = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_FAVORITE + " = " + NoteState.FAVORITE_YES.getValue();

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readFavoriteNotes, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                favoriteNotes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteNotes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> getAllNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> allNotes = new ArrayList<>();
        String readAllNotesQuery = "SELECT * FROM " + TABLE_NOTE;

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readAllNotesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                allNotes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allNotes;
    }

    public void updateNote(NoteModel updatedNote) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, updatedNote.getTitle());
        values.put(COLUMN_NOTE_CONTENT, updatedNote.getContent());
        values.put(COLUMN_NOTE_STATE, updatedNote.getState());
        values.put(COLUMN_NOTE_FAVORITE, updatedNote.getIsFavorite());
        values.put(COLUMN_NOTE_LAST_UPDATED, updatedNote.getLastUpdated());
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(updatedNote.getId())});
        db.close();
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void clear(int state) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_STATE + " = ?", new String[]{String.valueOf(state)});
        db.close();
    }
}