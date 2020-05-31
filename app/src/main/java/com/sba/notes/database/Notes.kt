package com.sba.notes.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Notes(
    @PrimaryKey(autoGenerate = true)
    var noteId:Long =0L,
    var date: Long =System.currentTimeMillis(),
    var title:String,
    var description:String
) :Serializable