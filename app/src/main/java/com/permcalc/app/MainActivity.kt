package com.permcalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.permcalc.app.ui.CalculatorScreen
import com.permcalc.app.ui.DisclaimerScreen
import com.permcalc.app.ui.LocalLocalizedContext
import com.permcalc.app.ui.SUPPORTED_LANGS
import com.permcalc.app.ui.defaultLang
import com.permcalc.app.ui.localizedContext
import com.permcalc.app.ui.theme.PermCalcTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("permcalc", MODE_PRIVATE)

        setContent {
            var accepted by remember { mutableStateOf(prefs.getBoolean(KEY_ACCEPTED, false)) }
            var lang by remember {
                mutableStateOf(prefs.getString(KEY_LANG, null) ?: defaultLang())
            }

            val localized = remember(lang) { localizedContext(this, lang) }

            PermCalcTheme {
                CompositionLocalProvider(LocalLocalizedContext provides localized) {
                    if (accepted) {
                        CalculatorScreen(
                            currentLang = lang,
                            onCycleLang = {
                                val idx = SUPPORTED_LANGS.indexOf(lang)
                                val next = SUPPORTED_LANGS[(idx + 1) % SUPPORTED_LANGS.size]
                                lang = next
                                prefs.edit().putString(KEY_LANG, next).apply()
                            },
                        )
                    } else {
                        DisclaimerScreen(
                            onAccept = {
                                prefs.edit().putBoolean(KEY_ACCEPTED, true).apply()
                                accepted = true
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_ACCEPTED = "disclaimer_accepted"
        private const val KEY_LANG = "lang"
    }
}
