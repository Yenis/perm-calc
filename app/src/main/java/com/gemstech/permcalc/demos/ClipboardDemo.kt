package com.gemstech.permcalc.demos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger

/** Longest text shown on screen; the full length is still reported. */
private const val MAX_TEXT_CHARS = 4000

/** Pasted images are downsampled to roughly this many pixels on the long edge. */
private const val MAX_IMAGE_EDGE = 1280

/** ClipDescription.EXTRA_IS_SENSITIVE, spelled out because the constant is API 33+. */
private const val EXTRA_IS_SENSITIVE = "android.content.extra.IS_SENSITIVE"

/**
 * Reads whatever is on the clipboard right now - text, images, or file links -
 * with no permission, because none exists. Android 10+ only lets the app that
 * has window focus (or the default keyboard) do this, so call it once the
 * window is focused again. Everything stays in memory.
 */
suspend fun runClipboardDemo(context: Context): ClipboardResult = withContext(Dispatchers.IO) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    val clip: ClipData? = clipboard.primaryClip
    val desc = clip?.description

    val pieces = ArrayList<ClipPiece>()
    val allText = StringBuilder()
    if (clip != null) {
        for (i in 0 until minOf(clip.itemCount, 5)) {
            val item = clip.getItemAt(i)
            val text = item.text?.toString() ?: item.htmlText
            val uri = item.uri
            when {
                text != null -> {
                    pieces.add(ClipText(text.take(MAX_TEXT_CHARS), text.length))
                    allText.appendLine(text)
                }
                uri != null -> pieces.add(readUri(context, uri))
                else -> item.coerceToText(context)?.toString()?.takeIf { it.isNotBlank() }?.let {
                    pieces.add(ClipText(it.take(MAX_TEXT_CHARS), it.length))
                    allText.appendLine(it)
                }
            }
        }
    }

    val sensitive = desc?.extras?.getBoolean(EXTRA_IS_SENSITIVE) == true
    ClipboardResult(
        items = pieces,
        label = desc?.label?.toString()?.takeIf { it.isNotBlank() },
        mimeTypes = desc?.let { d -> (0 until d.mimeTypeCount).map { d.getMimeType(it) } }.orEmpty(),
        copiedAtMillis = desc?.timestamp?.takeIf { it > 0 },
        markedSensitive = sensitive,
        findings = scanClipText(allText.toString(), sensitive),
    )
}

/**
 * The clipboard hands the reader a temporary grant to any content:// link on
 * it, so a copied photo can simply be opened.
 */
private fun readUri(context: Context, uri: Uri): ClipPiece {
    val resolver = context.contentResolver
    val mime = runCatching { resolver.getType(uri) }.getOrNull()
    if (mime == null || mime.startsWith("image/")) {
        val bitmap = runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_IMAGE_EDGE) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        }.getOrNull()
        if (bitmap != null) return ClipImage(bitmap, mime)
    }
    val name = runCatching {
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
    }.getOrNull()
    return ClipFile(uri.toString(), mime, name)
}

// ---------------------------------------------------------------------------
// The scanner. This is the part a malicious app would actually run: not a
// person reading your clipboard, but a few regexes checking every clip for
// things worth stealing or swapping.
// ---------------------------------------------------------------------------

private val EMAIL = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
private val URL = Regex("""(?i)\b(?:https?://|www\.)[^\s<>"]+""")
private val ETH = Regex("""\b0x[a-fA-F0-9]{40}\b""")
private val BTC = Regex("""\b(?:bc1[a-z0-9]{25,59}|[13][a-km-zA-HJ-NP-Z1-9]{25,34})\b""")
private val IBAN = Regex("""(?i)\b[A-Z]{2}\d{2}(?: ?[A-Z0-9]){11,30}\b""")
private val CARD = Regex("""\b(?:\d[ -]?){12,18}\d\b""")
private val PHONE = Regex("""(?<![\w+])(?:\+|00|0)\d[\d ()./-]{5,17}\d(?!\w)""")
private val OTP_ALONE = Regex("""^\s*(\d{4,8}|\d{3}[ -]\d{3})\s*$""")
private val OTP_KEYWORD = Regex(
    """(?i)(?:code|kod|código|кода?|pin|otp|tan)\D{0,20}?(\d{4,8})\b""",
)

fun scanClipText(text: String, markedSensitive: Boolean): List<ClipFinding> {
    if (text.isBlank()) return emptyList()
    val found = LinkedHashMap<FindingKind, MutableList<String>>()
    fun add(kind: FindingKind, value: String) {
        val list = found.getOrPut(kind) { mutableListOf() }
        if (value !in list && list.size < 3) list.add(value)
    }
    val trimmed = text.trim()

    if (markedSensitive || looksLikePassword(trimmed)) add(FindingKind.PASSWORD, trimmed)

    OTP_ALONE.find(trimmed)?.let { add(FindingKind.OTP, it.groupValues[1]) }
    OTP_KEYWORD.findAll(text).forEach { add(FindingKind.OTP, it.groupValues[1]) }

    val claimedDigits = HashSet<String>()
    CARD.findAll(text).forEach { m ->
        val digits = m.value.filter(Char::isDigit)
        if (digits.length in 13..19 && luhn(digits)) {
            add(FindingKind.CARD, m.value.trim())
            claimedDigits.add(digits)
        }
    }
    IBAN.findAll(text).forEach { m ->
        val compact = m.value.replace(" ", "").uppercase()
        if (ibanValid(compact)) {
            add(FindingKind.IBAN, m.value.trim())
            claimedDigits.add(compact.filter(Char::isDigit))
        }
    }
    ETH.findAll(text).forEach { add(FindingKind.CRYPTO, it.value) }
    BTC.findAll(text).forEach { m ->
        // Real addresses mix letters and digits; skip long runs of either.
        if (m.value.any(Char::isDigit) && m.value.any(Char::isLetter)) add(FindingKind.CRYPTO, m.value)
    }
    EMAIL.findAll(text).forEach { add(FindingKind.EMAIL, it.value) }
    if (OTP_ALONE.find(trimmed) == null) {
        PHONE.findAll(text).forEach { m ->
            val digits = m.value.filter(Char::isDigit)
            if (digits.length in 8..15 && claimedDigits.none { it.contains(digits) }) {
                add(FindingKind.PHONE, m.value.trim())
            }
        }
    }
    URL.findAll(text).forEach { add(FindingKind.URL, it.value.trimEnd('.', ',', ')', ';')) }

    // Kind order is the enum's: most dangerous first.
    return FindingKind.entries.flatMap { k -> found[k].orEmpty().map { ClipFinding(k, it) } }
}

/** One token, 8-128 chars, at least three of: lower, upper, digit, symbol. */
private fun looksLikePassword(s: String): Boolean {
    if (s.length !in 8..128 || s.any(Char::isWhitespace)) return false
    if (EMAIL.matches(s) || URL.containsMatchIn(s) || ETH.matches(s) || BTC.matches(s)) return false
    val classes = listOf(
        s.any(Char::isLowerCase),
        s.any(Char::isUpperCase),
        s.any(Char::isDigit),
        s.any { !it.isLetterOrDigit() },
    ).count { it }
    return classes >= 3
}

private fun luhn(digits: String): Boolean {
    var sum = 0
    digits.reversed().forEachIndexed { i, c ->
        var d = c - '0'
        if (i % 2 == 1) {
            d *= 2
            if (d > 9) d -= 9
        }
        sum += d
    }
    return sum % 10 == 0
}

/** ISO 13616 mod-97 check. */
private fun ibanValid(iban: String): Boolean {
    if (iban.length !in 15..34 || !iban.all { it.isLetterOrDigit() }) return false
    val rearranged = iban.drop(4) + iban.take(4)
    val numeric = buildString {
        rearranged.forEach { c -> if (c.isDigit()) append(c) else append(c - 'A' + 10) }
    }
    return runCatching { BigInteger(numeric).mod(BigInteger.valueOf(97)).toInt() == 1 }.getOrDefault(false)
}
