package com.ahmadini.velojot.presentation.screens.note_detail

import com.ahmadini.velojot.domain.model.ChecklistItem
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteType

data class NoteDetailUiState(
    val noteId: Long = 0,
    val title: String = "",
    val content: String = "",
    val noteType: NoteType = NoteType.TEXT,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isSaved: Boolean = false,
    val isDiscarded: Boolean = false,
    val showColorPicker: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val initialLoaded: Boolean = false
)
