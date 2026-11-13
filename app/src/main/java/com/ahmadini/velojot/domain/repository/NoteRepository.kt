package com.ahmadini.velojot.domain.repository

import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(
        filter: NoteFilter,
        searchQuery: String,
        sort: NoteSort = NoteSort.DATE_MODIFIED_DESC
    ): Flow<List<Note>>

    fun getNoteById(id: Long): Flow<Note?>

    suspend fun getNoteByIdDirect(id: Long): Note?

    suspend fun insertOrUpdate(note: Note): Long

    suspend fun deleteNote(id: Long)

    suspend fun togglePin(id: Long, isPinned: Boolean)

    suspend fun toggleArchive(id: Long, isArchived: Boolean)

    suspend fun updateColor(id: Long, color: NoteColor)

    suspend fun restoreNote(note: Note): Long
}
