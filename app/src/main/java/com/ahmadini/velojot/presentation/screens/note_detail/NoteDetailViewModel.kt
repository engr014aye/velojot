package com.ahmadini.velojot.presentation.screens.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteType
import com.ahmadini.velojot.domain.usecase.DeleteNoteUseCase
import com.ahmadini.velojot.domain.usecase.GetNoteByIdUseCase
import com.ahmadini.velojot.domain.usecase.SaveNoteUseCase
import com.ahmadini.velojot.domain.usecase.SaveResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface NoteDetailSideEffect {
    data class ShowSnackbar(val message: String) : NoteDetailSideEffect
    data object NavigateBack : NoteDetailSideEffect
}

class NoteDetailViewModel(
    private val noteId: Long,
    initialType: NoteType,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NoteDetailUiState(
            noteId = noteId,
            noteType = initialType
        )
    )
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val _sideEffects = MutableSharedFlow<NoteDetailSideEffect>()
    val sideEffects: SharedFlow<NoteDetailSideEffect> = _sideEffects.asSharedFlow()

    private var hasBeenSavedOrDiscarded = false

    init {
        if (noteId > 0) {
            viewModelScope.launch {
                val existingNote = getNoteByIdUseCase.direct(noteId)
                if (existingNote != null) {
                    _uiState.update {
                        it.copy(
                            noteId = existingNote.id,
                            title = existingNote.title,
                            content = existingNote.content,
                            noteType = existingNote.type,
                            checklistItems = existingNote.checklistItems,
                            color = existingNote.color,
                            isPinned = existingNote.isPinned,
                            isArchived = existingNote.isArchived,
                            initialLoaded = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(initialLoaded = true) }
                }
            }
        } else {
            _uiState.update { it.copy(initialLoaded = true) }
        }
    }

    fun onEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.title) }
            }
            is NoteDetailEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.content) }
            }
            is NoteDetailEvent.NoteTypeChanged -> {
                _uiState.update { it.copy(noteType = event.type) }
            }
            is NoteDetailEvent.ChecklistChanged -> {
                _uiState.update { it.copy(checklistItems = event.items) }
            }
            is NoteDetailEvent.ColorChanged -> {
                _uiState.update { it.copy(color = event.color, showColorPicker = false) }
            }
            is NoteDetailEvent.TogglePin -> {
                _uiState.update { it.copy(isPinned = !it.isPinned) }
            }
            is NoteDetailEvent.ToggleArchive -> {
                _uiState.update { it.copy(isArchived = !it.isArchived) }
            }
            is NoteDetailEvent.ToggleColorPicker -> {
                _uiState.update { it.copy(showColorPicker = !it.showColorPicker) }
            }
            is NoteDetailEvent.RequestDelete -> {
                _uiState.update { it.copy(showDeleteDialog = true) }
            }
            is NoteDetailEvent.DismissDelete -> {
                _uiState.update { it.copy(showDeleteDialog = false) }
            }
            is NoteDetailEvent.ConfirmDelete -> {
                viewModelScope.launch {
                    if (noteId > 0) {
                        deleteNoteUseCase(noteId)
                    }
                    hasBeenSavedOrDiscarded = true
                    _sideEffects.emit(NoteDetailSideEffect.ShowSnackbar("Note deleted"))
                    _sideEffects.emit(NoteDetailSideEffect.NavigateBack)
                }
            }
            is NoteDetailEvent.AutoSaveAndExit -> {
                autoSaveAndExit()
            }
        }
    }

    private fun autoSaveAndExit() {
        if (hasBeenSavedOrDiscarded) return
        hasBeenSavedOrDiscarded = true

        val current = _uiState.value
        val noteToSave = Note(
            id = current.noteId,
            title = current.title.trim(),
            content = current.content.trim(),
            type = current.noteType,
            checklistItems = current.checklistItems,
            color = current.color,
            isPinned = current.isPinned,
            isArchived = current.isArchived
        )

        viewModelScope.launch {
            when (val result = saveNoteUseCase(noteToSave)) {
                is SaveResult.DiscardedEmpty -> {
                    _sideEffects.emit(NoteDetailSideEffect.ShowSnackbar("Empty note discarded"))
                    _sideEffects.emit(NoteDetailSideEffect.NavigateBack)
                }
                is SaveResult.Saved -> {
                    _sideEffects.emit(NoteDetailSideEffect.NavigateBack)
                }
                is SaveResult.Error -> {
                    _sideEffects.emit(NoteDetailSideEffect.ShowSnackbar("Failed to save: ${result.message}"))
                    _sideEffects.emit(NoteDetailSideEffect.NavigateBack)
                }
            }
        }
    }

    class Factory(
        private val noteId: Long,
        private val initialType: NoteType,
        private val getNoteByIdUseCase: GetNoteByIdUseCase,
        private val saveNoteUseCase: SaveNoteUseCase,
        private val deleteNoteUseCase: DeleteNoteUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NoteDetailViewModel(
                noteId,
                initialType,
                getNoteByIdUseCase,
                saveNoteUseCase,
                deleteNoteUseCase
            ) as T
        }
    }
}
