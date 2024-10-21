package com.eipna.notable.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.eipna.notable.data.model.NoteModel;

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

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String noteQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTE + "(" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT, " +
                COLUMN_NOTE_CONTENT + " TEXT)";
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
        getWritableDatabase().insert(TABLE_NOTE, null, values);
        close();
    }

    public ArrayList<NoteModel> readNotes() {
        final ArrayList<NoteModel> notes = new ArrayList<>();
        String readQuery = "SELECT * FROM " + TABLE_NOTE;

        @SuppressLint("Recycle")
        Cursor cursor = getReadableDatabase().rawQuery(readQuery, null);
        while (cursor.moveToNext()) {
            notes.add(new NoteModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return notes;
    }

    public void updateNote(NoteModel note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        getWritableDatabase().update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(note.getNoteId())});
        close();
    }
}