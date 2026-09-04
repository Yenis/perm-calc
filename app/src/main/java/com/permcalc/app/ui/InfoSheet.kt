package com.permcalc.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.permcalc.app.R
import com.permcalc.app.demos.Demo
import com.permcalc.app.ui.theme.Palette

@Composable
fun InfoSheet(demo: Demo, onGrant: () -> Unit, onSkip: () -> Unit) {
    val res = demoInfoRes(demo)
    val scroll = rememberScrollState()

    BottomSheetDialog(onDismiss = onSkip) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 28.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Palette.outline)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(20.dp))

            Text(tr(res.infoTitle), color = Palette.onBg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(tr(res.infoSubtitle), color = Palette.dim, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(scroll),
            ) {
                InfoCard(
                    icon = "✅",
                    title = tr(R.string.legit_title),
                    headerColor = Palette.legitimateBg,
                    titleColor = Palette.legitimateText,
                    bulletColor = Palette.legitimateText,
                    items = trArray(res.legitimateArray),
                )
                Spacer(Modifier.height(10.dp))
                InfoCard(
                    icon = "⚠️",
                    title = tr(R.string.malicious_title),
                    headerColor = Palette.maliciousBg,
                    titleColor = Palette.maliciousText,
                    bulletColor = Palette.maliciousText,
                    items = trArray(res.maliciousArray),
                )
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = tr(R.string.common_grant), onClick = onGrant, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSkip)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(tr(R.string.common_skip), color = Palette.muted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: String,
    title: String,
    headerColor: Color,
    titleColor: Color,
    bulletColor: Color,
    items: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(icon, fontSize = 16.sp)
            Text(title.uppercase(), color = titleColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("•", color = bulletColor, fontSize = 16.sp)
                Text(item, color = Palette.onSurfaceVariant, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}
