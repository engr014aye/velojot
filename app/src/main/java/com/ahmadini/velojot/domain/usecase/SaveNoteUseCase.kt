package com.ahmadini.velojot.domain.usecase

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.repository.NoteRepository

sealed class SaveResult {
    data class Saved(val noteId: Long) : SaveResult()
    data object DiscardedEmpty : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): SaveResult {
        return try {
            if (note.isBlank) {
                // If existing note is cleared, delete it so ghost blanks don't linger
                if (note.id > 0) {
                    repository.deleteNote(note.id)
                }
                SaveResult.DiscardedEmpty
            } else {
                val updatedNote = note.copy(
                    modifiedAt = System.currentTimeMillis()
                )
                val id = repository.insertOrUpdate(updatedNote)
                SaveResult.Saved(id)
            }
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Failed to save note")
        }
    }
}
