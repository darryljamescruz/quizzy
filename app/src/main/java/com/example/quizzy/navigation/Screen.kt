package com.example.quizzy.navigation

// screen routes for navigation
sealed class Screen(val route: String) {
    object StudySetList : Screen("study_set_list")
    object CreateSet : Screen("create_set")
    object StudySetDetail : Screen("study_set_detail/{setTitle}") {
        fun createRoute(setTitle: String) = "study_set_detail/$setTitle"
    }
}


