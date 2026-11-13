package com.ahmadini.velojot.data.repository

import com.ahmadini.velojot.data.local.converter.Converters
import com.ahmadini.velojot.data.local.dao.NoteDao
import com.ahmadini.velojot.data.local.entity.NoteEntity
import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.domain.model.NoteSort
import com.ahmadini.velojot.domain.model.NoteType
import com.ahmadini.velojot.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    private val converters = Converters()

    override fun getNotes(
        filter: NoteFilter,
        searchQuery: String,
        sort: NoteSort
    ): Flow<List<Note>> {
        val trimmedQuery = searchQuery.trim()
        val entitiesFlow = when (filter) {
            NoteFilter.PINNED -> noteDao.getPinnedNotes(trimmedQuery)
            NoteFilter.CHECKLISTS -> noteDao.getChecklistNotes(trimmedQuery)
            NoteFilter.ARCHIVED -> noteDao.getArchivedNotes(trimmedQuery)
            NoteFilter.ALL -> when (sort) {
                NoteSort.DATE_MODIFIED_DESC -> noteDao.getActiveNotesByDateDesc(trimmedQuery)
                NoteSort.DATE_MODIFIED_ASC -> noteDao.getActiveNotesByDateAsc(trimmedQuery)
                NoteSort.TITLE_ASC -> noteDao.getActiveNotesByTitleAsc(trimmedQuery)
            }
        }
        return entitiesFlow.map { list -> list.map { it.toDomain(converters) } }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return noteDao.getNoteById(id).map { it?.toDomain(converters) }
    }

    override suspend fun getNoteByIdDirect(id: Long): Note? {
        return noteDao.getNoteByIdDirect(id)?.toDomain(converters)
    }

    override suspend fun insertOrUpdate(note: Note): Long {
        val entity = note.toEntity(converters)
        return noteDao.insertOrUpdate(entity)
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteById(id)
    }

    override suspend fun togglePin(id: Long, isPinned: Boolean) {
        noteDao.updatePinStatus(id, isPinned, System.currentTimeMillis())
    }

    override suspend fun toggleArchive(id: Long, isArchived: Boolean) {
        noteDao.updateArchiveStatus(id, isArchived, System.currentTimeMillis())
    }

    override suspend fun updateColor(id: Long, color: NoteColor) {
        noteDao.updateColor(id, color.id, System.currentTimeMillis())
    }

    override suspend fun restoreNote(note: Note): Long {
        val entity = note.toEntity(converters)
        return noteDao.insertOrUpdate(entity)
    }

    private fun NoteEntity.toDomain(converters: Converters): Note {
        val noteType = try {
            NoteType.valueOf(this.type)
        } catch (e: Exception) {
            NoteType.TEXT
        }
        return Note(
            id = this.id,
            title = this.title,
            content = this.content,
            type = noteType,
            checklistItems = converters.fromChecklistJson(this.checklistJson),
            color = NoteColor.fromId(this.colorId),
            isPinned = this.isPinned,
            isArchived = this.isArchived,
            createdAt = this.createdAt,
            modifiedAt = this.modifiedAt
        )
    }

    private fun Note.toEntity(converters: Converters): NoteEntity {
        return NoteEntity(
            id = this.id,
            title = this.title,
            content = this.content,
            type = this.type.name,
            checklistJson = converters.toChecklistJson(this.checklistItems),
            colorId = this.color.id,
            isPinned = this.isPinned,
            isArchived = this.isArchived,
            createdAt = this.createdAt,
            modifiedAt = this.modifiedAt
        )
    }
}
