package com.ahmadini.velojot.presentation.screens.note_detail

import com.ahmadini.velojot.domain.model.ChecklistItem
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteType

sealed interface NoteDetailEvent {
    data class TitleChanged(val title: String) : NoteDetailEvent
    data class ContentChanged(val content: String) : NoteDetailEvent
    data class NoteTypeChanged(val type: NoteType) : NoteDetailEvent
    data class ChecklistChanged(val items: List<ChecklistItem>) : NoteDetailEvent
    data class ColorChanged(val color: NoteColor) : NoteDetailEvent
    data object TogglePin : NoteDetailEvent
    data object ToggleArchive : NoteDetailEvent
    data object ToggleColorPicker : NoteDetailEvent
    data object RequestDelete : NoteDetailEvent
    data object DismissDelete : NoteDetailEvent
    data object ConfirmDelete : NoteDetailEvent
    data object AutoSaveAndExit : NoteDetailEvent
}
