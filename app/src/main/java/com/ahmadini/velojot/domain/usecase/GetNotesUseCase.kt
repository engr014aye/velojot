package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort
import com.ahmadini.velojot.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(
        filter: NoteFilter = NoteFilter.ALL,
        searchQuery: String = "",
        sort: NoteSort = NoteSort.DATE_MODIFIED_DESC
    ): Flow<List<Note>> {
        return repository.getNotes(filter, searchQuery, sort)
    }
}
