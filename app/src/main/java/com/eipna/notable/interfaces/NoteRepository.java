package com.eipna.notable.interfaces;

import com.eipna.notable.constants.NoteState;
import com.eipna.notable.models.NoteModel;

import java.util.ArrayList;

public interface NoteRepository {
    void createNote(NoteModel createdNote); // Note creation
    ArrayList<NoteModel> getNotes(); // Retrieves all notes
    ArrayList<NoteModel> getNotes(NoteState state); // Retrieves notes by state
    void updateNote(NoteModel updatedNote); // Updates a note
    void deleteNote(int noteId); // Deletes a note by a note Id
    void clearNotes(int state); // Clears notes by state
}