package com.ahmadini.velojot.presentation.screens.note_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteType
import com.ahmadini.velojot.presentation.components.ChecklistEditor
import com.ahmadini.velojot.presentation.components.ColorTagPicker
import com.ahmadini.velojot.presentation.components.DeleteConfirmDialog
import com.ahmadini.velojot.presentation.components.bouncyClickable
import com.ahmadini.velojot.presentation.theme.PrimaryNavy
import com.ahmadini.velojot.presentation.theme.RoseDelete
import com.ahmadini.velojot.presentation.theme.VibrantOrange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Handle back button / gesture to auto-save
    BackHandler {
        viewModel.onEvent(NoteDetailEvent.AutoSaveAndExit)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { effect ->
            when (effect) {
                is NoteDetailSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is NoteDetailSideEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    val pageBg = if (isDark) {
        if (uiState.color == NoteColor.DEFAULT) Color(0xFF070D19) else Color(uiState.color.darkSurfaceHex)
    } else {
        if (uiState.color == NoteColor.DEFAULT) Color(0xFFF8FAFC) else Color(uiState.color.lightSurfaceHex)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        containerColor = pageBg,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) Color(0xF2070D19) else Color(0xF5FFFFFF),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Button
                    IconButton(
                        onClick = { viewModel.onEvent(NoteDetailEvent.AutoSaveAndExit) },
                        modifier = Modifier
                            .bouncyClickable(scaleDown = 0.88f) {
                                viewModel.onEvent(NoteDetailEvent.AutoSaveAndExit)
                            }
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back & Save",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Actions Bar: Type Toggle, Palette, Pin, Delete, Save
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Switch between Text Note & Checklist
                        IconButton(
                            onClick = {
                                val nextType = if (uiState.noteType == NoteType.TEXT) NoteType.CHECKLIST else NoteType.TEXT
                                viewModel.onEvent(NoteDetailEvent.NoteTypeChanged(nextType))
                            },
                            modifier = Modifier.bouncyClickable(scaleDown = 0.88f) {
                                val nextType = if (uiState.noteType == NoteType.TEXT) NoteType.CHECKLIST else NoteType.TEXT
                                viewModel.onEvent(NoteDetailEvent.NoteTypeChanged(nextType))
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.noteType == NoteType.TEXT) Icons.Default.Checklist else Icons.Default.Description,
                                contentDescription = "Switch Note Format",
                                tint = VibrantOrange
                            )
                        }

                        // Color Palette Toggle
                        IconButton(
                            onClick = { viewModel.onEvent(NoteDetailEvent.ToggleColorPicker) },
                            modifier = Modifier.bouncyClickable(scaleDown = 0.88f) {
                                viewModel.onEvent(NoteDetailEvent.ToggleColorPicker)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(uiState.color.accentHex))
                                    .border(2.dp, if (isDark) Color.White else PrimaryNavy, CircleShape)
                            )
                        }

                        // Pin Toggle Button
                        IconButton(
                            onClick = { viewModel.onEvent(NoteDetailEvent.TogglePin) },
                            modifier = Modifier.bouncyClickable(scaleDown = 0.88f) {
                                viewModel.onEvent(NoteDetailEvent.TogglePin)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pin Note",
                                tint = if (uiState.isPinned) VibrantOrange else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Delete Button (shown if existing note)
                        if (uiState.noteId > 0) {
                            IconButton(
                                onClick = { viewModel.onEvent(NoteDetailEvent.RequestDelete) },
                                modifier = Modifier.bouncyClickable(scaleDown = 0.88f) {
                                    viewModel.onEvent(NoteDetailEvent.RequestDelete)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = RoseDelete
                                )
                            }
                        }

                        // Save / Confirm Checkmark
                        Box(
                            modifier = Modifier
                                .bouncyClickable(scaleDown = 0.90f) {
                                    viewModel.onEvent(NoteDetailEvent.AutoSaveAndExit)
                                }
                                .clip(RoundedCornerShape(12.dp))
                                .background(VibrantOrange)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PrimaryNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Expandable Color Palette Bar
            AnimatedVisibility(
                visible = uiState.showColorPicker,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    color = if (isDark) Color(0xFF0F1A2E) else Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ColorTagPicker(
                        selectedColor = uiState.color,
                        onColorSelected = {
                            viewModel.onEvent(NoteDetailEvent.ColorChanged(it))
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Content Area (Scrollable Title & Body / Checklist)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                // Title Field
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (uiState.title.isEmpty()) {
                        Text(
                            text = if (uiState.noteType == NoteType.TEXT) "Title..." else "Task list title...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    BasicTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onEvent(NoteDetailEvent.TitleChanged(it)) },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(VibrantOrange),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = if (isDark) Color(0xFF1E2D48) else Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                // Editor Body (Text or Checklist)
                when (uiState.noteType) {
                    NoteType.TEXT -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                        ) {
                            if (uiState.content.isEmpty()) {
                                Text(
                                    text = "Start jotting down your thoughts, plans, or ideas here...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    lineHeight = 24.sp
                                )
                            }
                            BasicTextField(
                                value = uiState.content,
                                onValueChange = { viewModel.onEvent(NoteDetailEvent.ContentChanged(it)) },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                ),
                                cursorBrush = SolidColor(VibrantOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp)
                            )
                        }
                    }
                    NoteType.CHECKLIST -> {
                        ChecklistEditor(
                            items = uiState.checklistItems,
                            onItemsChange = {
                                viewModel.onEvent(NoteDetailEvent.ChecklistChanged(it))
                            }
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (uiState.showDeleteDialog) {
            DeleteConfirmDialog(
                noteTitle = uiState.title,
                onConfirm = {
                    viewModel.onEvent(NoteDetailEvent.ConfirmDelete)
                },
                onDismiss = {
                    viewModel.onEvent(NoteDetailEvent.DismissDelete)
                }
            )
        }
    }
}
