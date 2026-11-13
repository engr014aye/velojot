package com.ahmadini.velojot.domain.model

data class ChecklistItem(
    val id: String,
    val text: String,
    val isDone: Boolean = false,
    val order: Int = 0
)
