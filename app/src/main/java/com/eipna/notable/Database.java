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
    private static final String TABLE_NOTE = "notes";
    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_CONTENT = "content";
    private static final String COLUMN_NOTE_DATE_CREATED = "date_created";
    private static final String COLUMN_NOTE_LAST_UPDATED = "last_updated";
    private static final String COLUMN_NOTE_STATUS = "status";
    private static final String COLUMN_NOTE_FAVORITE = "is_favorite";

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
                COLUMN_NOTE_STATUS + " INTEGER, " +
                COLUMN_NOTE_FAVORITE + " INTEGER)";
        sqLiteDatabase.execSQL(noteQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(sqLiteDatabase);
    }

    public void createNote(NoteModel note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_DATE_CREATED, note.getNoteDateCreated());
        values.put(COLUMN_NOTE_LAST_UPDATED, note.getNoteLastUpdated());
        values.put(COLUMN_NOTE_STATUS, note.getNoteState());
        values.put(COLUMN_NOTE_FAVORITE, note.getIsFavorite());
        getWritableDatabase().insert(TABLE_NOTE, null, values);
        close();
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readNotes(int noteStatus) {
        ArrayList<NoteModel> notes = new ArrayList<>();
        String readNotesQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STATUS + " = " + noteStatus;

        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery(readNotesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel note = new NoteModel();
                note.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                note.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                note.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                note.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATUS)));
                note.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readFavoriteNotes() {
        ArrayList<NoteModel> favoriteNotes = new ArrayList<>();
        String readFavoriteNotes = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_FAVORITE + " = " + NoteState.FAVORITE_YES.getValue();

        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery(readFavoriteNotes, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel note = new NoteModel();
                note.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                note.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                note.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                note.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATUS)));
                note.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                favoriteNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteNotes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readAllNotes() {
        ArrayList<NoteModel> allNotes = new ArrayList<>();
        String readAllNotesQuery = "SELECT * FROM " + TABLE_NOTE;

        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery(readAllNotesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel note = new NoteModel();
                note.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                note.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                note.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                note.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                note.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                note.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATUS)));
                note.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                allNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allNotes;
    }

    public void updateNote(NoteModel note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_LAST_UPDATED, note.getNoteLastUpdated());
        getWritableDatabase().update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(note.getNoteId())});
        close();
    }

    public void deleteNote(int noteId) {
        getWritableDatabase().delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        close();
    }

    public void alterNoteStatus(int noteId, int newNoteStatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_STATUS, newNoteStatus);
        getWritableDatabase().update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        close();
    }

    public void alterNoteFavorite(int noteId, int newValue) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_FAVORITE, newValue);
        getWritableDatabase().update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        close();
    }

    public void clearTrashNotes() {
        int deletedState = NoteState.DELETED.getValue();
        getWritableDatabase().delete(TABLE_NOTE, COLUMN_NOTE_STATUS + " = ?", new String[]{String.valueOf(deletedState)});
        close();
    }
}