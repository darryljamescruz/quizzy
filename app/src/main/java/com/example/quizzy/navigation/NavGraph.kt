package com.example.quizzy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quizzy.ui.screens.CreateFlashcardScreen
import com.example.quizzy.ui.screens.CreateSetScreen
import com.example.quizzy.ui.screens.StudyModeScreen
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
                },
                onNavigateToDetail = { setId ->
                    navController.navigate(Screen.StudySetDetail.createRoute(setId))
                },
                onNavigateToStudyMode = { setId ->
                    navController.navigate(Screen.StudyMode.createRoute(setId))
                }
            )
        }

        // Create new study set screen
        composable(Screen.CreateSet.route) {
            CreateSetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSetCreated = { setId ->
                    navController.navigate(Screen.StudySetDetail.createRoute(setId)) {
                        popUpTo(Screen.StudySetList.route)
                    }
                }
            )
        }

        // Study set detail screen
        composable(
            route = Screen.StudySetDetail.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId") ?: 0L
            StudySetDetailScreen(
                setId = setId,
                onNavigateBack = {
                    navController.popBackStack(Screen.StudySetList.route, inclusive = false)
                },
                onAddFlashcard = {
                    navController.navigate(Screen.CreateFlashcard.createRoute(setId))
                }
            )
        }

        // Create flashcard screen
        composable(
            route = Screen.CreateFlashcard.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId") ?: 0L
            CreateFlashcardScreen(
                setId = setId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFlashcardCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Study mode screen
        composable(
            route = Screen.StudyMode.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId") ?: 0L
            StudyModeScreen(
                setId = setId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}