package com.example.quizzy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quizzy.data.dao.FlashcardDao
import com.example.quizzy.data.dao.StudySetDao
import com.example.quizzy.data.models.Flashcard
import com.example.quizzy.data.models.StudySet

@Database(
    entities = [StudySet::class, Flashcard::class],
    version = 1,
    exportSchema = false
)
abstract class FlashcardDatabase : RoomDatabase() {

    abstract fun studySetDao(): StudySetDao

    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        fun getDatabase(context: Context): FlashcardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
