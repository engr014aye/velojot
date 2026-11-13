package com.ahmadini.velojot.presentation.navigation

import com.ahmadini.velojot.domain.model.NoteType

sealed class Screen(val route: String) {
    data object NotesList : Screen("notes_list")
    data object NoteDetail : Screen("note_detail/{noteId}?initialType={initialType}") {
        fun createRoute(noteId: Long = 0, initialType: NoteType = NoteType.TEXT): String {
            return "note_detail/$noteId?initialType=${initialType.name}"
        }
    }
}
