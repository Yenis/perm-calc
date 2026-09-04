package com.permcalc.app.ui

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.permcalc.app.R
import com.permcalc.app.demos.CameraResult
import com.permcalc.app.demos.ContactsResult
import com.permcalc.app.demos.Demo
import com.permcalc.app.demos.DemoResult
import com.permcalc.app.demos.LocationResult
import com.permcalc.app.demos.MicResult
import com.permcalc.app.demos.StorageResult
import com.permcalc.app.ui.theme.Palette
import java.io.File
import java.text.DateFormat
import java.util.Date
import kotlin.math.abs

@Composable
fun RevealSheet(demo: Demo, result: DemoResult, onClose: () -> Unit) {
    val scroll = rememberScrollState()
    val title = when (result) {
        is ContactsResult -> tr(R.string.contacts_reveal_title, result.contacts.size)
        is StorageResult -> tr(R.string.storage_reveal_title, result.count)
        is CameraResult -> tr(R.string.camera_reveal_title)
        is MicResult -> tr(R.string.microphone_reveal_title)
        is LocationResult -> tr(R.string.location_reveal_title)
    }

    BottomSheetDialog(onDismiss = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            // Title bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(title, color = Palette.onBg, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x10FFFFFF)))

            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(scroll)
                    .padding(top = 12.dp),
            ) {
                when (result) {
                    is CameraResult -> CameraReveal(result)
                    is MicResult -> MicReveal(result)
                    is ContactsResult -> ContactsReveal(result)
                    is LocationResult -> LocationReveal(result)
                    is StorageResult -> StorageReveal(result)
                }

                Spacer(Modifier.height(20.dp))
                WarningBanner(text = tr(demoInfoRes(demo).warning))
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = tr(R.string.common_close),
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                background = Palette.surfaceVariant,
                textColor = Palette.onBg,
            )
        }
    }
}

@Composable
private fun RevealDesc(text: String) {
    Text(text, color = Palette.onSurfaceVariant, fontSize = 14.sp, lineHeight = 22.sp)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun WarningBanner(text: String) {
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

@Composable
private fun CameraReveal(data: CameraResult) {
    RevealDesc(tr(R.string.camera_reveal_desc))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PhotoCard(path = data.frontPath, label = tr(R.string.camera_reveal_front), modifier = Modifier.weight(1f))
        PhotoCard(path = data.backPath, label = tr(R.string.camera_reveal_back), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PhotoCard(path: String?, label: String, modifier: Modifier = Modifier) {
    val bitmap = remember(path) {
        path?.let { runCatching { BitmapFactory.decodeFile(it) }.getOrNull() }
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.bg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.fillMaxWidth().aspectRatio(0.75f),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(Modifier.fillMaxWidth().aspectRatio(0.75f), contentAlignment = Alignment.Center) {
                Text("—", color = Palette.muted)
            }
        }
        Text(label, color = Palette.dim, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun MicReveal(data: MicResult) {
    var playing by remember { mutableStateOf(false) }
    val player = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose { runCatching { player.release() } }
    }

    RevealDesc(tr(R.string.microphone_reveal_desc, data.duration))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Palette.surfaceVariant)
            .clickable {
                if (playing) {
                    runCatching { player.stop(); player.reset() }
                    playing = false
                } else {
                    runCatching {
                        player.reset()
                        player.setDataSource(data.filePath)
                        player.prepare()
                        player.start()
                        player.setOnCompletionListener { playing = false }
                        playing = true
                    }
                }
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(if (playing) "⏹" else "▶", fontSize = 22.sp, color = Palette.accent)
        Text(
            if (playing) tr(R.string.microphone_stop) else tr(R.string.microphone_play),
            color = Palette.accent, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
        )
    }
    Spacer(Modifier.height(10.dp))
    Text(tr(R.string.microphone_reveal_file), color = Palette.muted, fontSize = 12.sp)
}

@Composable
private fun ContactsReveal(data: ContactsResult) {
    RevealDesc(tr(R.string.contacts_reveal_desc, data.contacts.size))
    if (data.contacts.isEmpty()) {
        EmptyText(tr(R.string.contacts_reveal_no_contacts))
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x10FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg),
    ) {
        val shown = data.contacts.take(50)
        shown.forEachIndexed { i, c ->
            if (i > 0) Divider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Palette.operator),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(c.name.firstOrNull()?.uppercase() ?: "?", color = Palette.accent, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text(c.name, color = Palette.onBg, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    c.phone?.let { Text(it, color = Palette.muted, fontSize = 12.sp) }
                    c.email?.let { Text(it, color = Palette.muted, fontSize = 12.sp) }
                }
            }
        }
        if (data.contacts.size > 50) {
            Text(
                tr(R.string.contacts_more, data.contacts.size - 50),
                color = Palette.muted, fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
            )
        }
    }
}

@Composable
private fun LocationReveal(data: LocationResult) {
    RevealDesc(tr(R.string.location_reveal_desc))

    val ctx = LocalLocalizedContext.current
    val rows = buildList {
        data.address?.let { a ->
            a.fullLine?.let { add(ctx.getString(R.string.location_reveal_address) to it) }
            a.street?.let { add(ctx.getString(R.string.location_label_street) to it) }
            a.neighborhood?.let { add(ctx.getString(R.string.location_label_neighborhood) to it) }
            a.city?.let { add(ctx.getString(R.string.location_label_city) to it) }
            a.postalCode?.let { add(ctx.getString(R.string.location_label_postal) to it) }
            a.district?.let { add(ctx.getString(R.string.location_label_district) to it) }
            a.region?.let { add(ctx.getString(R.string.location_label_region) to it) }
            a.country?.let { add(ctx.getString(R.string.location_label_country) to it) }
        }
        add(
            ctx.getString(R.string.location_reveal_coords) to
                "%.6f, %.6f".format(data.latitude, data.longitude),
        )
        add(ctx.getString(R.string.location_label_dms) to dms(data.latitude, data.longitude))
        data.altitude?.let { add(ctx.getString(R.string.location_label_altitude) to "%.0f m".format(it)) }
        data.accuracy?.let { add(ctx.getString(R.string.location_label_accuracy) to "±%d m".format(it.toInt())) }
        data.provider?.let { add(ctx.getString(R.string.location_label_source) to it.uppercase()) }
        data.timeMillis?.let {
            add(
                ctx.getString(R.string.location_label_time) to
                    DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(it)),
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x10FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg),
    ) {
        rows.forEachIndexed { i, (label, value) ->
            if (i > 0) Divider()
            LabeledRow(label, value)
        }
    }
}

/** Formats coordinates as degrees/minutes/seconds, e.g. 46°57'08.4"N  7°26'43.2"E. */
private fun dms(lat: Double, lng: Double): String {
    fun part(v: Double, pos: String, neg: String): String {
        val hemi = if (v >= 0) pos else neg
        val a = abs(v)
        val d = a.toInt()
        val minutesFull = (a - d) * 60
        val m = minutesFull.toInt()
        val s = (minutesFull - m) * 60
        return "%d°%02d'%04.1f\"%s".format(d, m, s, hemi)
    }
    return "${part(lat, "N", "S")}  ${part(lng, "E", "W")}"
}

@Composable
private fun StorageReveal(data: StorageResult) {
    RevealDesc(tr(R.string.storage_reveal_desc, data.count))
    if (data.items.isEmpty()) {
        EmptyText(tr(R.string.storage_reveal_no_media))
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x10FFFFFF), RoundedCornerShape(12.dp))
            .background(Palette.bg),
    ) {
        Text(
            tr(R.string.storage_reveal_recent_label).uppercase(),
            color = Palette.muted, fontSize = 11.sp,
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 6.dp),
        )
        data.items.take(30).forEachIndexed { i, item ->
            if (i > 0) Divider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (item.isVideo) "🎥" else "🖼️", fontSize = 18.sp)
                Column(Modifier.weight(1f)) {
                    Text(item.filename, color = Palette.onBg, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    item.dateMillis?.let {
                        Text(
                            DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(it)),
                            color = Palette.muted, fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(14.dp)) {
        Text(label.uppercase(), color = Palette.muted, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Palette.onBg, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x0DFFFFFF)))
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text, color = Palette.muted, fontSize = 14.sp, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp),
    )
}
