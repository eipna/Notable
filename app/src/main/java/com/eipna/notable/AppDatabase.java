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

public class AppDatabase extends SQLiteOpenHelper {

    // Database credentials
    private static final String DATABASE_NAME = "notable.db";
    private static final int DATABASE_VERSION = 1;

    // Note table and columns
    private static final String TABLE_NOTE = "notes";
    private static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_DATE_CREATED = "date_created";
    public static final String COLUMN_NOTE_LAST_UPDATED = "last_updated";
    public static final String COLUMN_NOTE_STATE = "state";
    public static final String COLUMN_NOTE_FAVORITE = "is_favorite";

    public AppDatabase(@Nullable Context context) {
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

    public void createNote(NoteModel note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_DATE_CREATED, note.getNoteDateCreated());
        values.put(COLUMN_NOTE_LAST_UPDATED, note.getNoteLastUpdated());
        values.put(COLUMN_NOTE_STATE, note.getNoteState());
        values.put(COLUMN_NOTE_FAVORITE, note.getIsFavorite());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readNotes(int noteStatus) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> notes = new ArrayList<>();
        String readNotesQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STATE + " = " + noteStatus;

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readNotesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                notes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readFavoriteNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> favoriteNotes = new ArrayList<>();
        String readFavoriteNotes = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_FAVORITE + " = " + NoteState.FAVORITE_YES.getValue();

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readFavoriteNotes, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                favoriteNotes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteNotes;
    }

    @SuppressLint("Range")
    public ArrayList<NoteModel> readAllNotes() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<NoteModel> allNotes = new ArrayList<>();
        String readAllNotesQuery = "SELECT * FROM " + TABLE_NOTE;

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(readAllNotesQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteModel retrievedNote = new NoteModel();
                retrievedNote.setNoteId(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)));
                retrievedNote.setNoteTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                retrievedNote.setNoteContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)));
                retrievedNote.setNoteDateCreated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_DATE_CREATED)));
                retrievedNote.setNoteLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_LAST_UPDATED)));
                retrievedNote.setNoteState(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_STATE)));
                retrievedNote.setIsFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_FAVORITE)));
                allNotes.add(retrievedNote);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allNotes;
    }

    public void updateNote(NoteModel note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_LAST_UPDATED, note.getNoteLastUpdated());
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(note.getNoteId())});
        db.close();
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void alterNoteStatus(int noteId, int newNoteStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_STATE, newNoteStatus);
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void alterNoteFavorite(int noteId, int newValue) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_FAVORITE, newValue);
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void clearTrashNotes() {
        SQLiteDatabase db = getWritableDatabase();
        int deletedState = NoteState.DELETED.getValue();
        db.delete(TABLE_NOTE, COLUMN_NOTE_STATE + " = ?", new String[]{String.valueOf(deletedState)});
        db.close();
    }
}