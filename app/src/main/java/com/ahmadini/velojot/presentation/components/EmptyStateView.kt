package com.ahmadini.velojot.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmadini.velojot.domain.model.NoteFilter
import com.ahmadini.velojot.presentation.theme.PrimaryNavy
import com.ahmadini.velojot.presentation.theme.VibrantOrange

@Composable
fun EmptyStateView(
    isSearching: Boolean,
    currentFilter: NoteFilter,
    onResetSearchOrFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val title = when {
        isSearching -> "No Results Found"
        currentFilter == NoteFilter.PINNED -> "No Pinned Notes"
        currentFilter == NoteFilter.CHECKLISTS -> "No Quick Tasks"
        currentFilter == NoteFilter.ARCHIVED -> "Archive Is Empty"
        else -> "Capture Thoughts Instantly"
    }

    val subtitle = when {
        isSearching -> "We couldn't find any notes matching your query. Check spelling or clear filters."
        currentFilter == NoteFilter.PINNED -> "Pin your most important thoughts and daily goals to keep them at the top."
        currentFilter == NoteFilter.CHECKLISTS -> "Create interactive checklists with checkboxes to keep everyday tasks organized."
        currentFilter == NoteFilter.ARCHIVED -> "Completed and archived notes will appear here."
        else -> "Tap the + button below to create your first offline note or interactive checklist."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Bespoke Vector Graphic drawn with Compose Canvas
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                val w = size.width
                val h = size.height

                val paperBg = if (isDark) Color(0xFF13223E) else Color(0xFFF1F5F9)
                val paperBorder = if (isDark) Color(0xFF23355C) else Color(0xFFCBD5E1)
                val lineCol = if (isDark) Color(0xFF1E3560) else Color(0xFFE2E8F0)
                val orange = Color(0xFFFF9900)

                // Backing angled sheet
                drawRoundRect(
                    color = paperBg.copy(alpha = 0.5f),
                    topLeft = Offset(w * 0.18f, h * 0.12f),
                    size = Size(w * 0.64f, h * 0.72f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // Front main notepad sheet
                drawRoundRect(
                    color = paperBg,
                    topLeft = Offset(w * 0.24f, h * 0.20f),
                    size = Size(w * 0.64f, h * 0.72f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                drawRoundRect(
                    color = paperBorder,
                    topLeft = Offset(w * 0.24f, h * 0.20f),
                    size = Size(w * 0.64f, h * 0.72f),
                    cornerRadius = CornerRadius(20f, 20f),
                    style = Stroke(width = 3f)
                )

                // Notebook Lines
                drawLine(
                    color = orange,
                    start = Offset(w * 0.32f, h * 0.34f),
                    end = Offset(w * 0.55f, h * 0.34f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = lineCol,
                    start = Offset(w * 0.32f, h * 0.46f),
                    end = Offset(w * 0.76f, h * 0.46f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = lineCol,
                    start = Offset(w * 0.32f, h * 0.58f),
                    end = Offset(w * 0.72f, h * 0.58f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = lineCol,
                    start = Offset(w * 0.32f, h * 0.70f),
                    end = Offset(w * 0.60f, h * 0.70f),
                    strokeWidth = 4f
                )

                // Stylized Flat Stylus / Pen Tip Nib
                val penPath = Path().apply {
                    moveTo(w * 0.70f, h * 0.64f)
                    lineTo(w * 0.88f, h * 0.46f)
                    lineTo(w * 0.94f, h * 0.52f)
                    lineTo(w * 0.76f, h * 0.70f)
                    close()
                }
                drawPath(penPath, color = orange)

                // Pen tip
                val tipPath = Path().apply {
                    moveTo(w * 0.70f, h * 0.64f)
                    lineTo(w * 0.65f, h * 0.75f)
                    lineTo(w * 0.76f, h * 0.70f)
                    close()
                }
                drawPath(tipPath, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        if (isSearching || currentFilter != NoteFilter.ALL) {
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onResetSearchOrFilter,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantOrange,
                    contentColor = PrimaryNavy
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.bouncyClickable(scaleDown = 0.92f, onClick = onResetSearchOrFilter)
            ) {
                Text(
                    text = if (isSearching) "Clear Search" else "View All Notes",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
