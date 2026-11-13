package com.ahmadini.velojot.domain.model

enum class NoteFilter(val label: String) {
    ALL("All Notes"),
    PINNED("Pinned"),
    CHECKLISTS("Quick Tasks"),
    ARCHIVED("Archived")
}

enum class NoteSort(val label: String) {
    DATE_MODIFIED_DESC("Recently Modified"),
    DATE_MODIFIED_ASC("Oldest Modified"),
    TITLE_ASC("Title (A-Z)")
}
