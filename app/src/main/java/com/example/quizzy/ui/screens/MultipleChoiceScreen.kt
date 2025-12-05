package com.example.quizzy.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizzy.data.FlashcardDatabase
import com.example.quizzy.ui.components.StudyCompletionDialog
import com.example.quizzy.ui.viewmodels.StudyModeViewModel
import com.example.quizzy.ui.viewmodels.StudyModeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceScreen(
    setId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { FlashcardDatabase.getDatabase(context) }
    val viewModel: StudyModeViewModel = viewModel(
        factory = StudyModeViewModelFactory(
            database.flashcardDao(),
            database.studySetDao(),
            setId
        )
    )
    val cards by viewModel.cards.collectAsState()
    val studySet by viewModel.studySet.collectAsState()
    val currentCard = viewModel.currentCard
    val progress = if (cards.isEmpty()) 0f else (viewModel.currentIndex + 1).toFloat() / cards.size
    val correctCount = cards.count { it.isKnown }
    val isLastCard = cards.isNotEmpty() && viewModel.currentIndex == cards.lastIndex

    // Generate wrong answers for multiple choice
    val wrongAnswers = remember(viewModel.currentIndex, cards) {
        if (currentCard != null && cards.size > 1) {
            cards.filter { it.cardId != currentCard.cardId }
                .shuffled()
                .take(3)
                .map { it.definition }
        } else {
            emptyList()
        }
    }

    val allChoices = remember(viewModel.currentIndex, currentCard, wrongAnswers) {
        if (currentCard != null) {
            (wrongAnswers + currentCard.definition).shuffled()
        } else {
            emptyList()
        }
    }

    var selectedAnswer by remember(viewModel.currentIndex) { mutableStateOf<String?>(null) }
    var showResult by remember(viewModel.currentIndex) { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            studySet?.title ?: "Multiple Choice",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            if (cards.isEmpty()) "0 / 0" else "${viewModel.currentIndex + 1} / ${cards.size}",
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
        if (currentCard == null || cards.size < 2) {
            // Not enough cards for multiple choice
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📝",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Need at least 4 flashcards for multiple choice!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Correct count badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$correctCount correct",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Question card with animation
                AnimatedContent(
                    targetState = viewModel.currentIndex,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        )
                    },
                    label = "questionSlide"
                ) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "QUESTION",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = currentCard.term,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF2C3E50)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Multiple choice options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    allChoices.forEachIndexed { index, choice ->
                        MultipleChoiceOption(
                            text = choice,
                            isSelected = selectedAnswer == choice,
                            isCorrect = choice == currentCard.definition,
                            showResult = showResult,
                            onClick = {
                                if (!showResult) {
                                    selectedAnswer = choice
                                    showResult = true
                                    // Mark as correct/incorrect
                                    if (choice == currentCard.definition) {
                                        viewModel.markKnown(autoAdvance = false)
                                    } else {
                                        viewModel.markUnknown(autoAdvance = false)
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = showResult) {
                    val answeredCorrectly = selectedAnswer == currentCard.definition
                    val feedbackColor = if (answeredCorrectly) Color(0xFF2E7D32) else Color(0xFFC62828)
                    val gradientColors = if (answeredCorrectly) {
                        listOf(Color(0xFF43A047), Color(0xFF81C784))
                    } else {
                        listOf(Color(0xFFE53935), Color(0xFFFF8A65))
                    }
                    val titleText = if (answeredCorrectly) "Correct answer!" else "Nice try!"
                    val messageText = if (answeredCorrectly) {
                        "Great job—keep the momentum going."
                    } else {
                        "Correct answer: ${currentCard.definition}"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, feedbackColor.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(gradientColors),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Icon(
                                        imageVector = if (answeredCorrectly) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = if (answeredCorrectly) "Correct" else "Incorrect",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .padding(10.dp)
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = titleText,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = messageText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Next button (only show after answering)
                if (showResult) {
                    Button(
                        onClick = {
                            if (!isLastCard) {
                                viewModel.nextCard()
                                selectedAnswer = null
                                showResult = false
                            } else {
                                showCompletionDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (!isLastCard) "Next Question →" else "Finish",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showCompletionDialog) {
        StudyCompletionDialog(
            onStudyAgain = {
                viewModel.resetProgress()
                selectedAnswer = null
                showResult = false
                showCompletionDialog = false
            },
            onBackToList = {
                showCompletionDialog = false
                onNavigateBack()
            },
            onDismissRequest = { showCompletionDialog = false }
        )
    }
}

@Composable
fun MultipleChoiceOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showResult && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
            showResult && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.15f)
            else -> Color.White
        },
        animationSpec = tween(300),
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            showResult && isCorrect -> Color(0xFF4CAF50)
            showResult && isSelected && !isCorrect -> Color(0xFFF44336)
            isSelected -> MaterialTheme.colorScheme.primary
            else -> Color.Gray.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Color(0xFF2C3E50),
                modifier = Modifier.weight(1f)
            )

            if (showResult) {
                if (isCorrect) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (isSelected) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Incorrect",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
