package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.repository.NoteRepository

class TogglePinUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long, currentPinnedState: Boolean) {
        repository.togglePin(id, !currentPinnedState)
    }
}
