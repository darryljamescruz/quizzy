package com.example.quizzy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Mock flashcard for now
data class Flashcard(
    val id: Int,
    val term: String,
    val definition: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySetDetailScreen(
    studySetTitle: String,
    onNavigateBack: () -> Unit,
    onAddFlashcard: () -> Unit
) {
    // Custom colors
    val sageGreen = Color(0xFF87A96B)
    val lightSage = Color(0xFFC8D5B9)
    val darkSage = Color(0xFF5F7C52)
    val creamBg = Color(0xFFF5F5DC)

    // Mock flashcards - empty for new sets
    val flashcards = remember { mutableStateListOf<Flashcard>() }

    Scaffold(
        containerColor = creamBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            studySetTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${flashcards.size} cards",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkSage.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = darkSage
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightSage,
                    titleContentColor = darkSage
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFlashcard,
                containerColor = sageGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
            }
        }
    ) { paddingValues ->
        if (flashcards.isEmpty()) {
            // Empty state - encouraging message
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎴",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Add your first flashcard!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = darkSage
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap the + button below to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAddFlashcard,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = sageGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Flashcard")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flashcards) { flashcard ->
                    FlashcardListItem(flashcard)
                }
            }
        }
    }
}

@Composable
fun FlashcardListItem(flashcard: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = flashcard.term,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = flashcard.definition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}