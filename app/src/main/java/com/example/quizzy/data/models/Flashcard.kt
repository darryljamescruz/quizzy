package com.example.quizzy.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = StudySet::class,
            parentColumns = ["setId"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["setId"])]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,
    val setId: Long,
    val term: String,
    val definition: String,
    val isKnown: Boolean = false
)
