package com.ahmadini.velojot.presentation.screens.notes_list

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort

data class NotesListUiState(
    val notes: List<Note> = emptyList(),
    val totalActiveNotesCount: Int = 0,
    val searchQuery: String = "",
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val selectedSort: NoteSort = NoteSort.DATE_MODIFIED_DESC,
    val selectedNoteForAction: Note? = null,
    val noteToDelete: Note? = null,
    val lastDeletedNote: Note? = null,
    val isLoading: Boolean = false
)
