package com.gemstech.permcalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gemstech.permcalc.ui.theme.Palette

/**
 * The language switcher. Shown in the calculator header and on the disclaimer,
 * so someone who cannot read the current language can change it before they are
 * asked to accept anything - which is also why the menu names each language in
 * its own script rather than translating the list.
 */
@Composable
fun LangChip(
    currentLang: String,
    onSelectLang: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Palette.surfaceVariant)
                .border(1.dp, Palette.outline, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(start = 12.dp, end = 9.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                currentLang.uppercase(),
                color = Palette.dim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text("\u25BE", color = Palette.dim, fontSize = 11.sp)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Palette.surface),
        ) {
            SUPPORTED_LANGS.forEach { code ->
                val selected = code == currentLang
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        if (!selected) onSelectLang(code)
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                code.uppercase(),
                                color = if (selected) Palette.accent else Palette.muted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                langDisplayName(code),
                                color = if (selected) Palette.accent else Palette.onBg,
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Palette.violet,
    textColor: Color = Color.White,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

/**
 * A bottom-anchored modal built on Dialog: a dimmed scrim behind a rounded
 * surface pinned to the bottom of the screen (mirrors the old React modals).
 */
@Composable
fun BottomSheetDialog(
    onDismiss: () -> Unit,
    dismissible: Boolean = true,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xD9000000)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Palette.surface),
            ) {
                content()
            }
        }
    }
}
