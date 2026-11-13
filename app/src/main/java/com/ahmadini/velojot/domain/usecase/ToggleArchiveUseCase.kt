package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.repository.NoteRepository

class ToggleArchiveUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long, currentArchivedState: Boolean) {
        repository.toggleArchive(id, !currentArchivedState)
    }
}
