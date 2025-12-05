package com.example.quizzy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizzy.data.FlashcardDatabase
import com.example.quizzy.data.models.Flashcard
import com.example.quizzy.ui.viewmodels.StudySetViewModel
import com.example.quizzy.ui.viewmodels.StudySetViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySetDetailScreen(
    setId: Long,
    onNavigateBack: () -> Unit,
    onAddFlashcard: () -> Unit
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
    val title = studySet?.title ?: "Study Set"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${flashcards.size} cards",
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
                actions = {
                    // "Done" button to finish and go back to main list
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            "Done",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFlashcard,
                containerColor = MaterialTheme.colorScheme.primary,
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap the + button below to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = flashcards,
                    key = { _, flashcard -> flashcard.cardId }
                ) { index, flashcard ->
                    FlashcardListItem(
                        flashcard = flashcard,
                        cardNumber = index + 1,
                        onEdit = { updatedTerm, updatedDefinition ->
                            viewModel.updateFlashcard(
                                flashcard.copy(
                                    term = updatedTerm,
                                    definition = updatedDefinition
                                )
                            )
                        },
                        onDelete = { viewModel.deleteFlashcard(flashcard) }
                    )
                }
            }
        }
    }
}

@Composable
fun FlashcardListItem(
    flashcard: Flashcard,
    cardNumber: Int,
    onEdit: (term: String, definition: String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTerm by remember { mutableStateOf(flashcard.term) }
    var editedDefinition by remember { mutableStateOf(flashcard.definition) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Flashcard?") },
            text = {
                Text("Are you sure you want to delete this flashcard?\n\nTerm: \"${flashcard.term}\"")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with card number and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question #$cardNumber",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    if (isEditing) {
                        // Save and Cancel buttons when editing
                        TextButton(
                            onClick = {
                                isEditing = false
                                editedTerm = flashcard.term
                                editedDefinition = flashcard.definition
                            }
                        ) {
                            Text("Cancel", color = Color.Gray)
                        }
                        TextButton(
                            onClick = {
                                onEdit(editedTerm, editedDefinition)
                                isEditing = false
                            },
                            enabled = editedTerm.isNotBlank() && editedDefinition.isNotBlank()
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Edit and Delete buttons when not editing
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditing) {
                // Edit mode - show text fields
                OutlinedTextField(
                    value = editedTerm,
                    onValueChange = { editedTerm = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedDefinition,
                    onValueChange = { editedDefinition = it },
                    label = { Text("Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            } else {
                // Display mode - show text
                Column {
                    Text(
                        text = "Question",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = flashcard.term,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Answer",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = flashcard.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}