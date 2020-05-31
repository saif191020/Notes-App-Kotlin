package com.sba.notes.database

import androidx.lifecycle.LiveData

class NotesRepository(private val noteDao: NotesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNotes: LiveData<List<Notes>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Notes) {
        noteDao.insertNote(note)
    }
    suspend fun updateNote(note: Notes) {
        noteDao.updateNote(note)
    }
    suspend fun deleteNote(note: Notes) {
        noteDao.deleteNote(note)
    }
}