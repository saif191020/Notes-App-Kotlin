package com.sba.notes.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteSaveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository
    init {
        val notesDao = NotesRoomDatabase.getDatabase(application,viewModelScope).notesDao()
        repository = NotesRepository(notesDao)
    }
    fun insertNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }
    fun updateNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
    }
}