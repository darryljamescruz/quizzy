package com.example.quizzy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quizzy.ui.screens.CreateSetScreen
import com.example.quizzy.ui.screens.StudySetDetailScreen
import com.example.quizzy.ui.screens.StudySetListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.StudySetList.route
    ) {
        // Main screen - list of study sets
        composable(Screen.StudySetList.route) {
            StudySetListScreen(
                onNavigateToCreateSet = {
                    navController.navigate(Screen.CreateSet.route)
                }
            )
        }

        // Create new study set screen
        composable(Screen.CreateSet.route) {
            CreateSetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSetCreated = { title ->
                    // Navigate to detail screen after creating
                    navController.navigate(Screen.StudySetDetail.createRoute(title)) {
                        popUpTo(Screen.StudySetList.route)
                    }
                }
            )
        }

        // Study set detail screen
        composable(
            route = Screen.StudySetDetail.route,
            arguments = listOf(
                navArgument("setTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val setTitle = backStackEntry.arguments?.getString("setTitle") ?: "Study Set"
            StudySetDetailScreen(
                studySetTitle = setTitle,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddFlashcard = {
                    // TODO: Navigate to add flashcard screen later
                }
            )
        }
    }
}