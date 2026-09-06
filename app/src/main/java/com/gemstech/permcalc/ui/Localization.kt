package com.gemstech.permcalc.ui

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/** Supported UI languages, listed by the language button in this order. */
val SUPPORTED_LANGS = listOf("en", "de", "bs", "es", "fr", "ru")

/**
 * Each language named in its own language - someone looking for their own
 * language needs to recognise it, not read its name in a language they do not
 * speak. Deliberately not localized.
 */
private val LANG_NAMES = mapOf(
    "en" to "English",
    "de" to "Deutsch",
    "bs" to "Bosanski",
    "es" to "Español",
    "fr" to "Français",
    "ru" to "Русский",
)

fun langDisplayName(code: String): String = LANG_NAMES[code] ?: code.uppercase()

fun defaultLang(): String {
    val sys = Locale.getDefault().language
    return if (sys in SUPPORTED_LANGS) sys else "en"
}

/** Builds a Context whose resources resolve to the given language. */
fun localizedContext(base: Context, lang: String): Context {
    val config = Configuration(base.resources.configuration)
    config.setLocale(Locale(lang))
    return base.createConfigurationContext(config)
}

/** Provides the language-specific Context to the composition. */
val LocalLocalizedContext = staticCompositionLocalOf<Context> {
    error("LocalLocalizedContext not provided")
}

@Composable
@ReadOnlyComposable
fun tr(@StringRes id: Int): String =
    LocalLocalizedContext.current.getString(id)

@Composable
@ReadOnlyComposable
fun tr(@StringRes id: Int, vararg args: Any): String =
    LocalLocalizedContext.current.getString(id, *args)

@Composable
@ReadOnlyComposable
fun trArray(@ArrayRes id: Int): List<String> =
    LocalLocalizedContext.current.resources.getStringArray(id).toList()
