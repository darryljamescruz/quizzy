package com.example.quizzy.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object StudySetList : Screen("study_set_list")
    object CreateSet : Screen("create_set")
    object StudySetDetail : Screen("study_set_detail/{setId}") {
        fun createRoute(setId: Long) = "study_set_detail/$setId"
    }
    object CreateFlashcard : Screen("create_flashcard/{setId}") {
        fun createRoute(setId: Long) = "create_flashcard/$setId"
    }
    object StudyMode : Screen("study_mode/{setId}") {
        fun createRoute(setId: Long) = "study_mode/$setId"
    }
    object StudyModeSelection : Screen("study_mode_selection/{setId}") {
        fun createRoute(setId: Long) = "study_mode_selection/$setId"
    }
    object MultipleChoice : Screen("multiple_choice/{setId}") {
        fun createRoute(setId: Long) = "multiple_choice/$setId"
    }
}