package com.example.quizzy.navigation

sealed class Screen(val route: String) {
    object StudySetList : Screen("study_set_list")
    object CreateSet : Screen("create_set")
    object StudySetDetail : Screen("study_set_detail/{setTitle}") {
        fun createRoute(setTitle: String) = "study_set_detail/$setTitle"
    }
    object CreateFlashcard : Screen("create_flashcard/{setTitle}") {
        fun createRoute(setTitle: String) = "create_flashcard/$setTitle"
    }
    object StudyMode : Screen("study_mode/{setTitle}") {
        fun createRoute(setTitle: String) = "study_mode/$setTitle"
    }
}