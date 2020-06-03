package com.sba.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Notes::class], version = 1, exportSchema = false)
public abstract class NotesRoomDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotesRoomDatabase? = null

        fun getDatabase(context: Context,scope: CoroutineScope): NotesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesRoomDatabase::class.java,
                    "note_database"
                ).addCallback(NoteDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.notesDao())
                }
            }
        }

        suspend fun populateDatabase(notesDao: NotesDao) {


            notesDao.insertNote(Notes(title = "Note Tip #4",description = "Enjoy Your New Notes App"))
            notesDao.insertNote(Notes(title = "Note Tip #3",description = "Done with a note? swipe it away on any side to Delete it.\n" +
                    "\n" +
                    "Give it a try! You Will have 5 sec to Undo.\n\nSwipe>>>>>>>>"))
            notesDao.insertNote(Notes(title = "Note Tip #2",description = "Three Dots in top right!\n\nClick it to change Theme or delete All Notes"))
            notesDao.insertNote(Notes(title = "Note Tip #1",description = "Notes lets you quickly capture what’s on your mind.\n" +
                    "\n" +
                    "To start a new note, use the \"Plus Button\" Below."))

        }
    }

}