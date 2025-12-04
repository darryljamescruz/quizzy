package com.example.quizzy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSetScreen(
    onNavigateBack: () -> Unit,
    onSetCreated: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Custom sage green colors
    val sageGreen = Color(0xFF87A96B)
    val lightSage = Color(0xFFC8D5B9)
    val darkSage = Color(0xFF5F7C52)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Study Set",
                        fontWeight = FontWeight.Bold
                    )
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header text
            Text(
                text = "Let's create something great!",
                style = MaterialTheme.typography.headlineSmall,
                color = darkSage,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Fill in the details below to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title field with icon
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Study Set Title") },
                placeholder = { Text("e.g., Spanish Vocabulary") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Title,
                        contentDescription = null,
                        tint = sageGreen
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sageGreen,
                    focusedLabelColor = sageGreen,
                    cursorColor = sageGreen
                )
            )

            // Description field with icon
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("What's this study set about?") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Description,
                        contentDescription = null,
                        tint = sageGreen
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = sageGreen,
                    focusedLabelColor = sageGreen,
                    cursorColor = sageGreen
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Create button with icon
            Button(
                onClick = {
                    // TODO: Save to database later
                    if (title.isNotBlank()) {
                        onSetCreated(title)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sageGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = title.isNotBlank()
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Create Study Set",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Helper text
            if (title.isBlank()) {
                Text(
                    text = "Please enter a title to continue",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}