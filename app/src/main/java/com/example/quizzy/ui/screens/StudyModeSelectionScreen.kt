package com.example.quizzy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizzy.data.FlashcardDatabase
import com.example.quizzy.ui.viewmodels.StudySetViewModel
import com.example.quizzy.ui.viewmodels.StudySetViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyModeSelectionScreen(
    setId: Long,
    onNavigateBack: () -> Unit,
    onSelectFlashcards: (Long) -> Unit,
    onSelectMultipleChoice: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = remember { FlashcardDatabase.getDatabase(context) }
    val viewModel: StudySetViewModel = viewModel(
        factory = StudySetViewModelFactory(
            database.studySetDao(),
            database.flashcardDao(),
            setId
        )
    )
    val studySet by viewModel.studySet.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Choose Study Mode",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            studySet?.title ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "How would you like to study?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${flashcards.size} flashcards",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Flashcards mode
            StudyModeCard(
                icon = "🎴",
                title = "Flashcards",
                description = "Flip cards to reveal answers. Classic study method.",
                onClick = { onSelectFlashcards(setId) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Multiple choice mode
            StudyModeCard(
                icon = "📝",
                title = "Multiple Choice",
                description = "Test yourself with 4 answer choices per question.",
                enabled = flashcards.size >= 4,
                onClick = { onSelectMultipleChoice(setId) },
                disabledMessage = if (flashcards.size < 4) "Need at least 4 cards" else null
            )
        }
    }
}

@Composable
fun StudyModeCard(
    icon: String,
    title: String,
    description: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    disabledMessage: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 4.dp else 1.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (enabled)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onBackground
                    else
                        Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (enabled) description else (disabledMessage ?: description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.6f)
                )
            }

            if (enabled) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
