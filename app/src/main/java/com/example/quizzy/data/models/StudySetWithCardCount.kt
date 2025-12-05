package com.example.quizzy.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class StudySetWithCardCount(
    @Embedded val studySet: StudySet,
    @ColumnInfo(name = "cardCount") val cardCount: Int
)
