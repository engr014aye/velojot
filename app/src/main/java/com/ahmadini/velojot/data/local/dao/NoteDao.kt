package com.ahmadini.velojot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ahmadini.velojot.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 0 
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY is_pinned DESC, modified_at DESC
    """)
    fun getActiveNotesByDateDesc(query: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 0 
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY is_pinned DESC, modified_at ASC
    """)
    fun getActiveNotesByDateAsc(query: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 0 
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY is_pinned DESC, title COLLATE NOCASE ASC
    """)
    fun getActiveNotesByTitleAsc(query: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 0 AND is_pinned = 1
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY modified_at DESC
    """)
    fun getPinnedNotes(query: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 0 AND type = 'CHECKLIST'
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY is_pinned DESC, modified_at DESC
    """)
    fun getChecklistNotes(query: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE is_archived = 1 
          AND (:query = '' OR title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR checklist_json LIKE '%' || :query || '%')
        ORDER BY modified_at DESC
    """)
    fun getArchivedNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdDirect(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE notes SET is_pinned = :isPinned, modified_at = :modifiedAt WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean, modifiedAt: Long)

    @Query("UPDATE notes SET is_archived = :isArchived, is_pinned = 0, modified_at = :modifiedAt WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean, modifiedAt: Long)

    @Query("UPDATE notes SET color_id = :colorId, modified_at = :modifiedAt WHERE id = :id")
    suspend fun updateColor(id: Long, colorId: String, modifiedAt: Long)
}
