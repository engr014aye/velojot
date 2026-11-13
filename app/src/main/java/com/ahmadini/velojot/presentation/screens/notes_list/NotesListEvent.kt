package com.ahmadini.velojot.presentation.screens.notes_list

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort

sealed interface NotesListEvent {
    data class SearchQueryChanged(val query: String) : NotesListEvent
    data class FilterSelected(val filter: NoteFilter) : NotesListEvent
    data class SortSelected(val sort: NoteSort) : NotesListEvent
    data class OpenActionSheet(val note: Note) : NotesListEvent
    data object CloseActionSheet : NotesListEvent
    data class TogglePin(val note: Note) : NotesListEvent
    data class ToggleArchive(val note: Note) : NotesListEvent
    data class UpdateColor(val note: Note, val color: NoteColor) : NotesListEvent
    data class RequestDeleteConfirmation(val note: Note) : NotesListEvent
    data object DismissDeleteConfirmation : NotesListEvent
    data class ConfirmDelete(val note: Note) : NotesListEvent
    data object UndoDelete : NotesListEvent
    data object ClearSearch : NotesListEvent
}
