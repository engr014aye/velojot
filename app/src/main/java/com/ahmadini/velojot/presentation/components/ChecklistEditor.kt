package com.ahmadini.velojot.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmadini.velojot.domain.model.ChecklistItem
import com.ahmadini.velojot.presentation.theme.MintSuccess
import com.ahmadini.velojot.presentation.theme.PrimaryNavy
import com.ahmadini.velojot.presentation.theme.VibrantOrange
import java.util.UUID

@Composable
fun ChecklistEditor(
    items: List<ChecklistItem>,
    onItemsChange: (List<ChecklistItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var newItemText by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Existing Items
        items.forEachIndexed { index, item ->
            ChecklistRowItem(
                item = item,
                onToggleDone = {
                    val updated = items.toMutableList()
                    updated[index] = item.copy(isDone = !item.isDone)
                    onItemsChange(updated)
                },
                onTextChanged = { newText ->
                    val updated = items.toMutableList()
                    updated[index] = item.copy(text = newText)
                    onItemsChange(updated)
                },
                onDelete = {
                    val updated = items.toMutableList()
                    updated.removeAt(index)
                    onItemsChange(updated)
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // New Item Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF131F37) else Color(0xFFF1F5F9))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(VibrantOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                    tint = VibrantOrange,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (newItemText.isEmpty()) {
                    Text(
                        text = "Add task item...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(VibrantOrange),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newItemText.isNotBlank()) {
                                val newItem = ChecklistItem(
                                    id = UUID.randomUUID().toString(),
                                    text = newItemText.trim(),
                                    isDone = false,
                                    order = items.size
                                )
                                onItemsChange(items + newItem)
                                newItemText = ""
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = newItemText.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .bouncyClickable(scaleDown = 0.9f) {
                            if (newItemText.isNotBlank()) {
                                val newItem = ChecklistItem(
                                    id = UUID.randomUUID().toString(),
                                    text = newItemText.trim(),
                                    isDone = false,
                                    order = items.size
                                )
                                onItemsChange(items + newItem)
                                newItemText = ""
                            }
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(VibrantOrange)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ChecklistRowItem(
    item: ChecklistItem,
    onToggleDone: () -> Unit,
    onTextChanged: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val checkBorderColor = if (item.isDone) MintSuccess else if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF0F1A2E) else Color(0xFFF8FAFC))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox button with bouncy tactile response
        Box(
            modifier = Modifier
                .size(24.dp)
                .bouncyClickable(scaleDown = 0.85f, onClick = onToggleDone)
                .clip(CircleShape)
                .background(if (item.isDone) MintSuccess else Color.Transparent)
                .border(2.dp, checkBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (item.isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // In-place editable text field
        BasicTextField(
            value = item.text,
            onValueChange = onTextChanged,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (item.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
            ),
            cursorBrush = SolidColor(VibrantOrange),
            modifier = Modifier.weight(1f)
        )

        // Delete item icon
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete task item",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
