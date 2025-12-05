package com.example.quizzy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quizzy.data.models.StudySet
import com.example.quizzy.data.models.StudySetWithCardCount
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySetDao {

    @Query(
        """
        SELECT study_sets.*, COUNT(flashcards.cardId) AS cardCount
        FROM study_sets
        LEFT JOIN flashcards ON study_sets.setId = flashcards.setId
        GROUP BY study_sets.setId
        ORDER BY study_sets.title ASC
        """
    )
    fun getStudySetsWithCardCount(): Flow<List<StudySetWithCardCount>>

    @Query("SELECT * FROM study_sets WHERE setId = :id")
    suspend fun getStudySetById(id: Long): StudySet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySet(studySet: StudySet): Long

    @Update
    suspend fun updateStudySet(studySet: StudySet)

    @Delete
    suspend fun deleteStudySet(studySet: StudySet)
}
