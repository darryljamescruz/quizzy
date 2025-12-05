package com.example.quizzy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.quizzy.data.models.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcards WHERE setId = :studySetId ORDER BY cardId ASC")
    fun getFlashcardsForSet(studySetId: Long): Flow<List<Flashcard>>

    @Insert
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)

    @Query("UPDATE flashcards SET isKnown = :known WHERE cardId = :cardId")
    suspend fun updateKnownStatus(cardId: Long, known: Boolean)
}
