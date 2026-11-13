package com.ahmadini.velojot.presentation.screens.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort
import com.ahmadini.velojot.domain.usecase.DeleteNoteUseCase
import com.ahmadini.velojot.domain.usecase.GetNotesUseCase
import com.ahmadini.velojot.domain.usecase.ToggleArchiveUseCase
import com.ahmadini.velojot.domain.usecase.TogglePinUseCase
import com.ahmadini.velojot.domain.usecase.UpdateNoteColorUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class FilterParams(
    val query: String = "",
    val filter: NoteFilter = NoteFilter.ALL,
    val sort: NoteSort = NoteSort.DATE_MODIFIED_DESC
)

private data class DialogsState(
    val selectedNoteForAction: Note? = null,
    val noteToDelete: Note? = null,
    val lastDeletedNote: Note? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val toggleArchiveUseCase: ToggleArchiveUseCase,
    private val updateNoteColorUseCase: UpdateNoteColorUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(NoteFilter.ALL)
    private val _selectedSort = MutableStateFlow(NoteSort.DATE_MODIFIED_DESC)
    private val _selectedNoteForAction = MutableStateFlow<Note?>(null)
    private val _noteToDelete = MutableStateFlow<Note?>(null)
    private val _lastDeletedNote = MutableStateFlow<Note?>(null)

    private val _filterParams = combine(_searchQuery, _selectedFilter, _selectedSort) { query, filter, sort ->
        FilterParams(query, filter, sort)
    }

    private val _filteredNotes = _filterParams.flatMapLatest { params ->
        getNotesUseCase(params.filter, params.query, params.sort)
    }

    private val _totalActiveNotes = getNotesUseCase(NoteFilter.ALL, "")

    private val _dialogsState = combine(_selectedNoteForAction, _noteToDelete, _lastDeletedNote) { actionNote, deleteNote, lastDeleted ->
        DialogsState(actionNote, deleteNote, lastDeleted)
    }

    val uiState: StateFlow<NotesListUiState> = combine(
        _filteredNotes,
        _totalActiveNotes,
        _filterParams,
        _dialogsState
    ) { notes, allNotes, params, dialogs ->
        NotesListUiState(
            notes = notes,
            totalActiveNotesCount = allNotes.size,
            searchQuery = params.query,
            selectedFilter = params.filter,
            selectedSort = params.sort,
            selectedNoteForAction = dialogs.selectedNoteForAction,
            noteToDelete = dialogs.noteToDelete,
            lastDeletedNote = dialogs.lastDeletedNote,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesListUiState(isLoading = true)
    )

    fun onEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            is NotesListEvent.FilterSelected -> {
                _selectedFilter.value = event.filter
            }
            is NotesListEvent.SortSelected -> {
                _selectedSort.value = event.sort
            }
            is NotesListEvent.OpenActionSheet -> {
                _selectedNoteForAction.value = event.note
            }
            is NotesListEvent.CloseActionSheet -> {
                _selectedNoteForAction.value = null
            }
            is NotesListEvent.TogglePin -> {
                viewModelScope.launch {
                    togglePinUseCase(event.note.id, event.note.isPinned)
                    _selectedNoteForAction.value = null
                }
            }
            is NotesListEvent.ToggleArchive -> {
                viewModelScope.launch {
                    toggleArchiveUseCase(event.note.id, event.note.isArchived)
                    _selectedNoteForAction.value = null
                }
            }
            is NotesListEvent.UpdateColor -> {
                viewModelScope.launch {
                    updateNoteColorUseCase(event.note.id, event.color)
                    _selectedNoteForAction.value = null
                }
            }
            is NotesListEvent.RequestDeleteConfirmation -> {
                _selectedNoteForAction.value = null
                _noteToDelete.value = event.note
            }
            is NotesListEvent.DismissDeleteConfirmation -> {
                _noteToDelete.value = null
            }
            is NotesListEvent.ConfirmDelete -> {
                viewModelScope.launch {
                    _lastDeletedNote.value = event.note
                    deleteNoteUseCase(event.note.id)
                    _noteToDelete.value = null
                }
            }
            is NotesListEvent.UndoDelete -> {
                val toRestore = _lastDeletedNote.value
                if (toRestore != null) {
                    viewModelScope.launch {
                        deleteNoteUseCase.restore(toRestore)
                        _lastDeletedNote.value = null
                    }
                }
            }
            is NotesListEvent.ClearSearch -> {
                _searchQuery.value = ""
                _selectedFilter.value = NoteFilter.ALL
            }
        }
    }

    class Factory(
        private val getNotesUseCase: GetNotesUseCase,
        private val deleteNoteUseCase: DeleteNoteUseCase,
        private val togglePinUseCase: TogglePinUseCase,
        private val toggleArchiveUseCase: ToggleArchiveUseCase,
        private val updateNoteColorUseCase: UpdateNoteColorUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotesListViewModel(
                getNotesUseCase,
                deleteNoteUseCase,
                togglePinUseCase,
                toggleArchiveUseCase,
                updateNoteColorUseCase
            ) as T
        }
    }
}
