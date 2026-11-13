package com.ahmadini.velojot.domain.model

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val type: NoteType = NoteType.TEXT,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
) {
    val isBlank: Boolean
        get() = when (type) {
            NoteType.TEXT -> title.trim().isEmpty() && content.trim().isEmpty()
            NoteType.CHECKLIST -> title.trim().isEmpty() && checklistItems.none { it.text.trim().isNotEmpty() }
        }

    val completedChecklistCount: Int
        get() = checklistItems.count { it.isDone }

    val totalChecklistCount: Int
        get() = checklistItems.size
}
