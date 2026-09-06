package com.gemstech.permcalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gemstech.permcalc.R
import com.gemstech.permcalc.ui.theme.Palette

@Composable
fun DisclaimerScreen(
    currentLang: String,
    onSelectLang: (String) -> Unit,
    onAccept: () -> Unit,
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.bg)
            .systemBarsPadding()
            .verticalScroll(scroll)
            .padding(horizontal = 28.dp)
            .padding(top = 12.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            LangChip(currentLang = currentLang, onSelectLang = onSelectLang)
        }

        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Palette.surface)
                .border(1.dp, Palette.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("🔐", fontSize = 44.sp)
        }

        Spacer(Modifier.height(20.dp))
        Text("PermCalc", color = Palette.accent, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(6.dp))
        Text(
            tr(R.string.disclaimer_title).uppercase(),
            color = Palette.onBg,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            tr(R.string.disclaimer_subtitle),
            color = Palette.muted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Palette.surface)
                .border(1.dp, Palette.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(20.dp),
        ) {
            Text(tr(R.string.disclaimer_body), color = Palette.onSurfaceVariant, fontSize = 15.sp, lineHeight = 24.sp)
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Palette.legitimateBg)
                .border(1.dp, Palette.legitimateBorder, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("🛡️", fontSize = 18.sp)
            Text(tr(R.string.disclaimer_note), color = Palette.legitimateText, fontSize = 13.sp, lineHeight = 20.sp)
        }

        Spacer(Modifier.height(32.dp))
        PrimaryButton(
            text = tr(R.string.common_understand),
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
