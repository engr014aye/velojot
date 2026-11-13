package com.ahmadini.velojot.domain.model

enum class NoteColor(
    val id: String,
    val displayName: String,
    val lightSurfaceHex: Long,
    val darkSurfaceHex: Long,
    val accentHex: Long
) {
    DEFAULT("default", "Classic Navy", 0xFFFFFFFF, 0xFF0D172A, 0xFF001845),
    CORAL("coral", "Coral Blossom", 0xFFFFF1F0, 0xFF2D1619, 0xFFFF6B6B),
    AMBER("amber", "Sunset Amber", 0xFFFFFBEB, 0xFF2B210B, 0xFFFF9900),
    EMERALD("emerald", "Sage Mint", 0xFFF0FDF4, 0xFF0D281E, 0xFF10B981),
    SKY("sky", "Sky Blue", 0xFFF0F9FF, 0xFF0C243B, 0xFF0EA5E9),
    INDIGO("indigo", "Velvet Indigo", 0xFFEEF2FF, 0xFF191D3B, 0xFF6366F1),
    PURPLE("purple", "Soft Lavender", 0xFFFAF5FF, 0xFF231435, 0xFFA855F7),
    ROSE("rose", "Rose Quartz", 0xFFFFF1F2, 0xFF2E121B, 0xFFF43F5E),
    SLATE("slate", "Slate Grey", 0xFFF8FAFC, 0xFF172033, 0xFF64748B);

    companion object {
        fun fromId(id: String?): NoteColor {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
        }
    }
}
