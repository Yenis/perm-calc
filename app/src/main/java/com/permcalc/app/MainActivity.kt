package com.permcalc.app

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.permcalc.app.demos.Demo
import com.permcalc.app.demos.permissionsFor
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
            // Disclaimer acceptance is intentionally NOT persisted: every fresh
            // launch starts un-accepted so each viewer gets the first-time flow.
            var accepted by remember { mutableStateOf(false) }
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
                        DisclaimerScreen(onAccept = { accepted = true })
                    }
                }
            }
        }
    }

    /**
     * Reset the app to a first-time state when it leaves the foreground: schedule
     * revocation of every runtime permission we hold, applied once the app is
     * killed. Combined with the non-persisted disclaimer, reopening the app after
     * closing it gives a clean first-time experience with no manual steps in
     * Android Settings.
     *
     * revokeSelfPermissionsOnKill() requires API 33 (Android 13). On older
     * versions there is no API for an app to revoke its own permissions, so this
     * is a no-op there.
     */
    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isChangingConfigurations) {
            val granted = Demo.ALL
                .flatMap { permissionsFor(it).toList() }
                .distinct()
                .filter { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            if (granted.isNotEmpty()) {
                runCatching { revokeSelfPermissionsOnKill(granted) }
            }
        }
    }

    companion object {
        private const val KEY_LANG = "lang"
    }
}
