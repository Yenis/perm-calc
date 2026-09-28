package com.gemstech.permcalc

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
import com.gemstech.permcalc.demos.Demo
import com.gemstech.permcalc.demos.permissionsFor
import com.gemstech.permcalc.ui.CalculatorScreen
import com.gemstech.permcalc.ui.DisclaimerScreen
import com.gemstech.permcalc.ui.LocalLocalizedContext
import com.gemstech.permcalc.ui.defaultLang
import com.gemstech.permcalc.ui.localizedContext
import com.gemstech.permcalc.ui.theme.PermCalcTheme

class MainActivity : ComponentActivity() {

    /**
     * Set while the clipboard demo is open. That demo asks the user to leave the
     * app and come back, and the reset below kills the process as soon as the
     * app is backgrounded - which would throw them back to the disclaimer.
     */
    var holdPermissionReset = false

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
                    // Shared by both screens: the switcher is on the disclaimer
                    // too, so someone who cannot read the current language can
                    // change it before being asked to accept anything.
                    val selectLang: (String) -> Unit = { next ->
                        lang = next
                        prefs.edit().putString(KEY_LANG, next).apply()
                    }

                    if (accepted) {
                        CalculatorScreen(
                            currentLang = lang,
                            onSelectLang = selectLang,
                        )
                    } else {
                        DisclaimerScreen(
                            currentLang = lang,
                            onSelectLang = selectLang,
                            onAccept = { accepted = true },
                        )
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isChangingConfigurations && !holdPermissionReset) {
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
