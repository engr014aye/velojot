package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.repository.NoteRepository

class UpdateNoteColorUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long, color: NoteColor) {
        repository.updateColor(id, color)
    }
}
