package com.example.quizzy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzy.data.dao.StudySetDao
import com.example.quizzy.data.models.StudySet
import com.example.quizzy.data.models.StudySetWithCardCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudySetListViewModel(
    private val studySetDao: StudySetDao
) : ViewModel() {

    private val _studySets = MutableStateFlow<List<StudySetWithCardCount>>(emptyList())
    val studySets: StateFlow<List<StudySetWithCardCount>> = _studySets.asStateFlow()

    init {
        observeStudySets()
    }

    private fun observeStudySets() {
        viewModelScope.launch {
            studySetDao.getStudySetsWithCardCount().collect { sets ->
                _studySets.value = sets
            }
        }
    }

    suspend fun createStudySet(title: String, description: String): Long {
        return withContext(Dispatchers.IO) {
            val newSet = StudySet(title = title, description = description)
            studySetDao.insertStudySet(newSet)
        }
    }

    fun deleteStudySet(studySet: StudySet) {
        viewModelScope.launch(Dispatchers.IO) {
            studySetDao.deleteStudySet(studySet)
        }
    }
}
