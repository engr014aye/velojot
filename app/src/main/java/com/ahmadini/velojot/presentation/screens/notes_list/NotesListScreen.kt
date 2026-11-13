package com.ahmadini.velojot.presentation.screens.notes_list

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteType
import com.ahmadini.velojot.presentation.components.ActionBottomSheet
import com.ahmadini.velojot.presentation.components.DeleteConfirmDialog
import com.ahmadini.velojot.presentation.components.EmptyStateView
import com.ahmadini.velojot.presentation.components.GlassmorphicHeader
import com.ahmadini.velojot.presentation.components.NoteCard
import com.ahmadini.velojot.presentation.components.bouncyClickable
import com.ahmadini.velojot.presentation.theme.PrimaryNavy
import com.ahmadini.velojot.presentation.theme.VibrantOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    onNavigateToCreateNote: (type: NoteType) -> Unit,
    onNavigateToNoteDetail: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFabMenu by remember { mutableStateOf(false) }

    // Listen for delete events to trigger Undo Snackbar
    LaunchedEffect(uiState.lastDeletedNote) {
        val deletedNote = uiState.lastDeletedNote
        if (deletedNote != null) {
            val title = if (deletedNote.title.isNotBlank()) deletedNote.title else "Note"
            val result = snackbarHostState.showSnackbar(
                message = "\"$title\" deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(NotesListEvent.UndoDelete)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.navigationBarsPadding()
            ) {
                // Secondary expanded options with spring animation
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = fadeIn() + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        // Create Checklist Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .bouncyClickable(scaleDown = 0.92f) {
                                    showFabMenu = false
                                    onNavigateToCreateNote(NoteType.CHECKLIST)
                                }
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(4.dp, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "New Checklist",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VibrantOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = null,
                                    tint = VibrantOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Create Note Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .bouncyClickable(scaleDown = 0.92f) {
                                    showFabMenu = false
                                    onNavigateToCreateNote(NoteType.TEXT)
                                }
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(4.dp, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "New Note",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryNavy.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Main Bouncing FAB
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = VibrantOrange,
                    contentColor = PrimaryNavy,
                    shape = CircleShape,
                    modifier = Modifier.bouncyClickable(scaleDown = 0.90f) {
                        showFabMenu = !showFabMenu
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create note or task",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Glassmorphic Header & Search
            GlassmorphicHeader(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = {
                    viewModel.onEvent(NotesListEvent.SearchQueryChanged(it))
                },
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = {
                    viewModel.onEvent(NotesListEvent.FilterSelected(it))
                },
                selectedSort = uiState.selectedSort,
                onSortSelected = {
                    viewModel.onEvent(NotesListEvent.SortSelected(it))
                },
                totalNotesCount = uiState.totalActiveNotesCount
            )

            // Content Area: Empty State or Animated Staggered Grid
            if (uiState.notes.isEmpty()) {
                EmptyStateView(
                    isSearching = uiState.searchQuery.isNotEmpty(),
                    currentFilter = uiState.selectedFilter,
                    onResetSearchOrFilter = {
                        viewModel.onEvent(NotesListEvent.ClearSearch)
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(
                        items = uiState.notes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNavigateToNoteDetail(note.id) },
                            onLongClick = {
                                viewModel.onEvent(NotesListEvent.OpenActionSheet(note))
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }

        // Action Bottom Sheet
        uiState.selectedNoteForAction?.let { actionNote ->
            ActionBottomSheet(
                note = actionNote,
                sheetState = bottomSheetState,
                onDismiss = {
                    viewModel.onEvent(NotesListEvent.CloseActionSheet)
                },
                onTogglePin = {
                    viewModel.onEvent(NotesListEvent.TogglePin(actionNote))
                },
                onToggleArchive = {
                    viewModel.onEvent(NotesListEvent.ToggleArchive(actionNote))
                },
                onColorSelected = { color ->
                    viewModel.onEvent(NotesListEvent.UpdateColor(actionNote, color))
                },
                onShare = {
                    viewModel.onEvent(NotesListEvent.CloseActionSheet)
                    shareNoteText(context, actionNote)
                },
                onDelete = {
                    viewModel.onEvent(NotesListEvent.RequestDeleteConfirmation(actionNote))
                }
            )
        }

        // Delete Confirmation Dialog
        uiState.noteToDelete?.let { deletingNote ->
            DeleteConfirmDialog(
                noteTitle = deletingNote.title,
                onConfirm = {
                    viewModel.onEvent(NotesListEvent.ConfirmDelete(deletingNote))
                },
                onDismiss = {
                    viewModel.onEvent(NotesListEvent.DismissDeleteConfirmation)
                }
            )
        }
    }
}

private fun shareNoteText(context: Context, note: Note) {
    val shareContent = buildString {
        if (note.title.isNotBlank()) {
            appendLine(note.title)
            appendLine()
        }
        when (note.type) {
            NoteType.TEXT -> append(note.content)
            NoteType.CHECKLIST -> {
                note.checklistItems.forEach { item ->
                    val checkMark = if (item.isDone) "[x]" else "[ ]"
                    appendLine("$checkMark ${item.text}")
                }
            }
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, note.title.ifBlank { "VeloJot Note" })
        putExtra(Intent.EXTRA_TEXT, shareContent.trim())
    }
    context.startActivity(Intent.createChooser(intent, "Share Note"))
}
