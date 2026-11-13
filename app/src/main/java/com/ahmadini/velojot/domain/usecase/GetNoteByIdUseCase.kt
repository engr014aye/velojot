package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteByIdUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(id: Long): Flow<Note?> {
        return repository.getNoteById(id)
    }

    suspend fun direct(id: Long): Note? {
        return repository.getNoteByIdDirect(id)
    }
}
