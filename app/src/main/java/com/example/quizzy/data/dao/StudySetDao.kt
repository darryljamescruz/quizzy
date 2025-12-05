package com.example.quizzy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quizzy.data.models.StudySet
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySetDao {

    @Query("SELECT * FROM study_sets ORDER BY title ASC")
    fun getAllStudySets(): Flow<List<StudySet>>

    @Query("SELECT * FROM study_sets WHERE setId = :id")
    suspend fun getStudySetById(id: Long): StudySet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySet(studySet: StudySet): Long

    @Update
    suspend fun updateStudySet(studySet: StudySet)

    @Delete
    suspend fun deleteStudySet(studySet: StudySet)
}
