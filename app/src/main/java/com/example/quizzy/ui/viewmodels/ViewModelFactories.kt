package com.example.quizzy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzy.data.dao.FlashcardDao
import com.example.quizzy.data.dao.StudySetDao

class StudySetListViewModelFactory(
    private val studySetDao: StudySetDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudySetListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudySetListViewModel(studySetDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class StudySetViewModelFactory(
    private val studySetDao: StudySetDao,
    private val flashcardDao: FlashcardDao,
    private val setId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudySetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudySetViewModel(studySetDao, flashcardDao, setId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class StudyModeViewModelFactory(
    private val flashcardDao: FlashcardDao,
    private val studySetDao: StudySetDao,
    private val setId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyModeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyModeViewModel(flashcardDao, studySetDao, setId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
