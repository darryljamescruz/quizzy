package com.example.quizzy.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sets")
data class StudySet(
    @PrimaryKey(autoGenerate = true)
    val setId: Long = 0,
    val title: String,
    val description: String = ""
)
