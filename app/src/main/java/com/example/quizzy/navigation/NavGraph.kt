package com.example.quizzy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quizzy.ui.screens.CreateFlashcardScreen
import com.example.quizzy.ui.screens.CreateSetScreen
import com.example.quizzy.ui.screens.MultipleChoiceScreen
import com.example.quizzy.ui.screens.StudyModeScreen
import com.example.quizzy.ui.screens.StudyModeSelectionScreen
import com.example.quizzy.ui.screens.StudySetDetailScreen
import com.example.quizzy.ui.screens.StudySetListScreen
import com.example.quizzy.ui.screens.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            val navigateToSets: () -> Unit = {
                navController.navigate(Screen.StudySetList.route) {
                    popUpTo(Screen.Welcome.route) { inclusive = true }
                }
            }
            WelcomeScreen(
                onStartStudying = navigateToSets,
                onViewSets = navigateToSets
            )
        }

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
                    navController.navigate(Screen.StudyModeSelection.createRoute(setId))
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

        // Study mode selection screen
        composable(
            route = Screen.StudyModeSelection.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId") ?: 0L
            StudyModeSelectionScreen(
                setId = setId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectFlashcards = { setId ->
                    navController.navigate(Screen.StudyMode.createRoute(setId))
                },
                onSelectMultipleChoice = { setId ->
                    navController.navigate(Screen.MultipleChoice.createRoute(setId))
                }
            )
        }

        // Flashcards study mode screen
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

        // Multiple choice study mode screen
        composable(
            route = Screen.MultipleChoice.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId") ?: 0L
            MultipleChoiceScreen(
                setId = setId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}