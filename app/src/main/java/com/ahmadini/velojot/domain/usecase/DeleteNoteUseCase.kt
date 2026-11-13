package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.repository.NoteRepository

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteNote(id)
    }

    suspend fun restore(note: Note): Long {
        return repository.restoreNote(note)
    }
}
