package com.ahmadini.velojot.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ahmadini.velojot.domain.model.Note
import com.ahmadini.velojot.domain.model.NoteColor
import com.ahmadini.velojot.presentation.theme.RoseDelete
import com.ahmadini.velojot.presentation.theme.VibrantOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    note: Note,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onColorSelected: (NoteColor) -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0F1A2E) else Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            // Header: Note summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(note.color.accentHex))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (note.title.isNotBlank()) note.title else "Untitled Note",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Picker Palette Section
            ColorTagPicker(
                selectedColor = note.color,
                onColorSelected = onColorSelected
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF1E2D4A) else Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Pin / Unpin
            ActionItem(
                icon = Icons.Default.PushPin,
                title = if (note.isPinned) "Unpin from Top" else "Pin to Top",
                iconTint = VibrantOrange,
                onClick = onTogglePin
            )

            // Share Note
            ActionItem(
                icon = Icons.Default.Share,
                title = "Share Note Text",
                iconTint = MaterialTheme.colorScheme.onSurface,
                onClick = onShare
            )

            // Archive / Unarchive
            ActionItem(
                icon = if (note.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                title = if (note.isArchived) "Unarchive Note" else "Archive Note",
                iconTint = MaterialTheme.colorScheme.onSurface,
                onClick = onToggleArchive
            )

            // Delete Note
            ActionItem(
                icon = Icons.Default.Delete,
                title = "Delete Note",
                iconTint = RoseDelete,
                textColor = RoseDelete,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
