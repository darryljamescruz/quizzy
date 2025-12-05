package com.example.quizzy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzy.data.dao.FlashcardDao
import com.example.quizzy.data.dao.StudySetDao
import com.example.quizzy.data.models.Flashcard
import com.example.quizzy.data.models.StudySet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudySetViewModel(
    private val studySetDao: StudySetDao,
    private val flashcardDao: FlashcardDao,
    private val setId: Long
) : ViewModel() {

    private val _studySet = MutableStateFlow<StudySet?>(null)
    val studySet: StateFlow<StudySet?> = _studySet.asStateFlow()

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    init {
        loadStudySet()
        observeFlashcards()
    }

    private fun loadStudySet() {
        viewModelScope.launch(Dispatchers.IO) {
            _studySet.value = studySetDao.getStudySetById(setId)
        }
    }

    private fun observeFlashcards() {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardDao.getFlashcardsForSet(setId).collect { cards ->
                _flashcards.value = cards
            }
        }
    }

    suspend fun addFlashcard(term: String, definition: String) {
        withContext(Dispatchers.IO) {
            val flashcard = Flashcard(
                setId = setId,
                term = term,
                definition = definition
            )
            flashcardDao.insertFlashcard(flashcard)
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardDao.deleteFlashcard(flashcard)
        }
    }
}
