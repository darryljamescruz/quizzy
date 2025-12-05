package com.example.quizzy.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

class StudyModeViewModel(
    private val flashcardDao: FlashcardDao,
    private val studySetDao: StudySetDao,
    private val setId: Long
) : ViewModel() {

    private val _cards = MutableStateFlow<List<Flashcard>>(emptyList())
    val cards: StateFlow<List<Flashcard>> = _cards.asStateFlow()

    private val _studySet = MutableStateFlow<StudySet?>(null)
    val studySet: StateFlow<StudySet?> = _studySet.asStateFlow()

    var currentIndex by mutableStateOf(0)
        private set

    var showAnswer by mutableStateOf(false)
        private set

    val currentCard: Flashcard?
        get() = _cards.value.getOrNull(currentIndex)

    init {
        loadStudySet()
        observeCards()
    }

    private fun loadStudySet() {
        viewModelScope.launch(Dispatchers.IO) {
            _studySet.value = studySetDao.getStudySetById(setId)
        }
    }

    private fun observeCards() {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardDao.getFlashcardsForSet(setId).collect { fetchedCards ->
                _cards.value = fetchedCards
                if (currentIndex >= fetchedCards.size) {
                    currentIndex = (fetchedCards.size - 1).coerceAtLeast(0)
                    showAnswer = false
                }
            }
        }
    }

    fun flipCard() {
        showAnswer = !showAnswer
    }

    fun nextCard() {
        if (currentIndex < _cards.value.lastIndex) {
            currentIndex++
            showAnswer = false
        }
    }

    fun previousCard() {
        if (currentIndex > 0) {
            currentIndex--
            showAnswer = false
        }
    }

    fun markKnown(autoAdvance: Boolean = true) {
        updateKnownStatus(isKnown = true, autoAdvance = autoAdvance)
    }

    fun markUnknown(autoAdvance: Boolean = true) {
        updateKnownStatus(isKnown = false, autoAdvance = autoAdvance)
    }

    private fun updateKnownStatus(isKnown: Boolean, autoAdvance: Boolean) {
        val card = currentCard ?: return
        viewModelScope.launch(Dispatchers.IO) {
            flashcardDao.updateKnownStatus(card.cardId, isKnown)
        }
        if (autoAdvance) {
            advanceAfterAnswer()
        }
    }

    private fun advanceAfterAnswer() {
        showAnswer = false
        if (currentIndex < _cards.value.lastIndex) {
            currentIndex++
        }
    }

    fun resetProgress() {
        if (_cards.value.isEmpty()) return
        currentIndex = 0
        showAnswer = false
    }
}
