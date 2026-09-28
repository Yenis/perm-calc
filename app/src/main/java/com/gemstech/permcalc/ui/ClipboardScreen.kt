package com.gemstech.permcalc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.gemstech.permcalc.MainActivity
import com.gemstech.permcalc.R
import com.gemstech.permcalc.demos.ClipFile
import com.gemstech.permcalc.demos.ClipFinding
import com.gemstech.permcalc.demos.ClipImage
import com.gemstech.permcalc.demos.ClipText
import com.gemstech.permcalc.demos.ClipboardResult
import com.gemstech.permcalc.demos.FindingKind
import com.gemstech.permcalc.demos.runClipboardDemo
import com.gemstech.permcalc.ui.theme.Palette
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

/**
 * The clipboard demo lives on its own screen because it works nothing like the
 * others: there is no permission dialog and no background phase. It asks the
 * user to leave, copy something anywhere, and come back - and reads the
 * clipboard the moment the app regains focus. Reading it on return is not a
 * loophole in the demo; it is exactly what any foreground app can do.
 */
@Composable
fun ClipboardScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var result by remember { mutableStateOf<ClipboardResult?>(null) }
    // True once the user has left the app at least once, so we do not "reveal"
    // whatever happened to be on the clipboard before they engaged with it.
    var hasLeft by remember { mutableStateOf(false) }

    // Keep the process alive while this screen is up: MainActivity.onStop would
    // otherwise schedule a permission reset and bounce us to the disclaimer.
    DisposableEffect(activity) {
        activity?.holdPermissionReset = true
        onDispose { activity?.holdPermissionReset = false }
    }

    // Read the clipboard each time the app comes back to the foreground.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> hasLeft = true
                Lifecycle.Event.ON_RESUME -> if (hasLeft) {
                    scope.launch { result = runClipboardDemo(context) }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.bg)
            .systemBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("📋  ${tr(R.string.clipboard_info_title)}", color = Palette.onBg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Palette.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    tr(R.string.common_close),
                    color = Palette.onBg, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickableNoRipple(onClose),
                )
            }
        }

        val current = result
        if (current == null) {
            WaitingState()
        } else {
            ClipboardReveal(current)
        }
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick,
)

@Composable
private fun WaitingState() {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        // The instruction the whole demo hinges on.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Palette.surface)
                .border(1.dp, Palette.permButtonBorder, RoundedCornerShape(16.dp))
                .padding(20.dp),
        ) {
            Text(tr(R.string.clipboard_wait_title), color = Palette.permButtonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(tr(R.string.clipboard_wait_body), color = Palette.onSurfaceVariant, fontSize = 14.sp, lineHeight = 22.sp)
            Spacer(Modifier.height(14.dp))
            listOf(
                tr(R.string.clipboard_wait_step1),
                tr(R.string.clipboard_wait_step2),
                tr(R.string.clipboard_wait_step3),
            ).forEachIndexed { i, step ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${i + 1}.", color = Palette.accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(step, color = Palette.onBg, fontSize = 14.sp, lineHeight = 21.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(tr(R.string.clipboard_wait_nopermission), color = Palette.maliciousText, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun ClipboardReveal(data: ClipboardResult) {
    val scroll = rememberScrollState()
    val ctx = LocalLocalizedContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(top = 12.dp, bottom = 28.dp),
    ) {
        val empty = data.items.isEmpty()
        Text(
            if (empty) tr(R.string.clipboard_reveal_empty_title) else tr(R.string.clipboard_reveal_title),
            color = Palette.onBg, fontSize = 20.sp, fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (empty) tr(R.string.clipboard_reveal_empty_desc) else tr(R.string.clipboard_reveal_desc),
            color = Palette.onSurfaceVariant, fontSize = 14.sp, lineHeight = 22.sp,
        )
        Spacer(Modifier.height(16.dp))

        if (!empty) {
            // Metadata the app also gets, for free, with the content.
            val meta = buildList {
                data.label?.let { add(ctx.getString(R.string.clipboard_meta_label) to it) }
                data.copiedAtMillis?.let {
                    add(ctx.getString(R.string.clipboard_meta_copied) to DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(it)))
                }
                if (data.mimeTypes.isNotEmpty()) add(ctx.getString(R.string.clipboard_meta_type) to data.mimeTypes.joinToString(", "))
                if (data.markedSensitive) add(ctx.getString(R.string.clipboard_meta_sensitive) to ctx.getString(R.string.clipboard_meta_sensitive_yes))
            }

            data.items.forEach { piece ->
                when (piece) {
                    is ClipText -> ClipTextCard(piece)
                    is ClipImage -> ClipImageCard(piece)
                    is ClipFile -> ClipFileCard(piece)
                }
                Spacer(Modifier.height(12.dp))
            }

            if (meta.isNotEmpty()) {
                MetaCard(meta)
                Spacer(Modifier.height(12.dp))
            }

            if (data.findings.isNotEmpty()) {
                FindingsCard(data.findings)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        WarningCard(tr(R.string.clipboard_warning))
    }
}

@Composable
private fun ClipTextCard(piece: ClipText) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg)
            .padding(14.dp),
    ) {
        Text(tr(R.string.clipboard_piece_text).uppercase(), color = Palette.muted, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Text(piece.text, color = Palette.onBg, fontSize = 14.sp, lineHeight = 21.sp, fontFamily = FontFamily.Monospace)
        if (piece.totalLength > piece.text.length) {
            Spacer(Modifier.height(8.dp))
            Text(tr(R.string.clipboard_text_truncated, piece.totalLength), color = Palette.muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ClipImageCard(piece: ClipImage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg)
            .padding(14.dp),
    ) {
        Text(tr(R.string.clipboard_piece_image).uppercase(), color = Palette.muted, fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        Image(
            bitmap = piece.bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ClipFileCard(piece: ClipFile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg)
            .padding(14.dp),
    ) {
        Text(tr(R.string.clipboard_piece_file).uppercase(), color = Palette.muted, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        piece.name?.let { Text(it, color = Palette.onBg, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
        piece.mimeType?.let { Text(it, color = Palette.muted, fontSize = 12.sp) }
        Text(piece.uri, color = Palette.muted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun MetaCard(rows: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg),
    ) {
        rows.forEachIndexed { i, (label, value) ->
            if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x0DFFFFFF)))
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text(label.uppercase(), color = Palette.muted, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                Text(value, color = Palette.onBg, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * The findings card is the blunt part: it shows the demo running the same cheap
 * scan a malicious app would, and naming what it would have targeted.
 */
@Composable
private fun FindingsCard(findings: List<ClipFinding>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.maliciousBg)
            .border(1.dp, Palette.maliciousBorder, RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🎯", fontSize = 16.sp)
            Text(tr(R.string.clipboard_findings_title).uppercase(), color = Palette.maliciousText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            tr(R.string.clipboard_findings_desc),
            color = Palette.onSurfaceVariant, fontSize = 13.sp, lineHeight = 20.sp,
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
        )
        findings.forEach { f ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Palette.warningBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(tr(findingLabel(f.kind)).uppercase(), color = Palette.maliciousText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(f.value, color = Palette.onBg, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

private fun findingLabel(kind: FindingKind): Int = when (kind) {
    FindingKind.PASSWORD -> R.string.clipboard_find_password
    FindingKind.OTP -> R.string.clipboard_find_otp
    FindingKind.CARD -> R.string.clipboard_find_card
    FindingKind.IBAN -> R.string.clipboard_find_iban
    FindingKind.CRYPTO -> R.string.clipboard_find_crypto
    FindingKind.EMAIL -> R.string.clipboard_find_email
    FindingKind.PHONE -> R.string.clipboard_find_phone
    FindingKind.URL -> R.string.clipboard_find_url
}

@Composable
private fun WarningCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.warningBg)
            .border(1.dp, Palette.warningBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("⚠️", fontSize = 18.sp)
        Text(text, color = Palette.maliciousText, fontSize = 13.sp, lineHeight = 20.sp)
    }
}
