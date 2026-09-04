package com.permcalc.app.ui

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.permcalc.app.CalculatorState
import com.permcalc.app.R
import com.permcalc.app.demos.Demo
import com.permcalc.app.demos.DemoResult
import com.permcalc.app.demos.MIC_SECONDS
import com.permcalc.app.demos.formatTime
import com.permcalc.app.demos.isGranted
import com.permcalc.app.demos.permissionsFor
import com.permcalc.app.demos.runCameraDemo
import com.permcalc.app.demos.runContactsDemo
import com.permcalc.app.demos.runLocationDemo
import com.permcalc.app.demos.runMicrophoneDemo
import com.permcalc.app.demos.runStorageDemo
import com.permcalc.app.ui.theme.Palette
import kotlinx.coroutines.launch

private enum class Overlay { CAMERA, MIC, LOADING }

@Composable
fun CalculatorScreen(currentLang: String, onCycleLang: () -> Unit) {
    val calc = remember { CalculatorState() }
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val localized = LocalLocalizedContext.current
    val scope = rememberCoroutineScope()

    var activeDemo by remember { mutableStateOf<Demo?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    var revealResult by remember { mutableStateOf<DemoResult?>(null) }

    var overlay by remember { mutableStateOf<Overlay?>(null) }
    var cameraFront by remember { mutableStateOf(true) }
    var micTick by remember { mutableIntStateOf(0) }
    var loadingRes by remember { mutableIntStateOf(R.string.contacts_demo_loading) }

    var pendingDemo by remember { mutableStateOf<Demo?>(null) }

    fun toast(msg: String) = Toast.makeText(localized, msg, Toast.LENGTH_LONG).show()

    fun startDemo(demo: Demo) {
        scope.launch {
            try {
                when (demo) {
                    Demo.CAMERA -> {
                        cameraFront = true
                        overlay = Overlay.CAMERA
                        val res = runCameraDemo(activity) { front -> cameraFront = front }
                        overlay = null
                        revealResult = res; activeDemo = demo
                    }
                    Demo.MICROPHONE -> {
                        micTick = 0
                        overlay = Overlay.MIC
                        val res = runMicrophoneDemo(context) { t -> micTick = t }
                        overlay = null
                        revealResult = res; activeDemo = demo
                    }
                    else -> {
                        loadingRes = when (demo) {
                            Demo.CONTACTS -> R.string.contacts_demo_loading
                            Demo.LOCATION -> R.string.location_demo_loading
                            else -> R.string.storage_demo_loading
                        }
                        overlay = Overlay.LOADING
                        val res = when (demo) {
                            Demo.CONTACTS -> runContactsDemo(context)
                            Demo.LOCATION -> runLocationDemo(context)
                            else -> runStorageDemo(context)
                        }
                        overlay = null
                        revealResult = res; activeDemo = demo
                    }
                }
            } catch (e: Exception) {
                overlay = null
                toast(e.message ?: "Error")
            }
        }
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { resultMap ->
        val demo = pendingDemo
        pendingDemo = null
        if (demo != null) {
            if (isGranted(demo, resultMap)) {
                startDemo(demo)
            } else {
                toast(localized.getString(R.string.common_denied_msg))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.bg)
            .systemBarsPadding(),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("PERMCALC", color = Palette.accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Palette.surfaceVariant)
                    .border(1.dp, Palette.outline, RoundedCornerShape(8.dp))
                    .clickable(onClick = onCycleLang)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(currentLang.uppercase(), color = Palette.dim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Display
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            CalcDisplay(value = calc.display, operator = calc.operator)
        }

        // Keypad
        Keypad(calc)

        // Divider label
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(Palette.divider))
            Text(tr(R.string.perm_demos_label), color = Palette.dividerLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Box(Modifier.weight(1f).height(1.dp).background(Palette.divider))
        }

        // Permission buttons
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).padding(bottom = 8.dp)) {
            Demo.ALL.forEach { demo ->
                PermButton(
                    icon = demoIcon(demo),
                    label = tr(permButtonLabelRes(demo)),
                    modifier = Modifier.weight(1f),
                    onClick = { activeDemo = demo; showInfo = true },
                )
            }
        }
    }

    // Info sheet
    val infoDemo = activeDemo
    if (showInfo && infoDemo != null) {
        InfoSheet(
            demo = infoDemo,
            onGrant = {
                showInfo = false
                pendingDemo = infoDemo
                permLauncher.launch(permissionsFor(infoDemo))
            },
            onSkip = { showInfo = false; activeDemo = null },
        )
    }

    // Reveal sheet
    val result = revealResult
    val revealDemo = activeDemo
    if (result != null && revealDemo != null && !showInfo) {
        RevealSheet(
            demo = revealDemo,
            result = result,
            onClose = { revealResult = null; activeDemo = null },
        )
    }

    // Busy overlays
    when (overlay) {
        Overlay.CAMERA -> CenterOverlay {
            Text(
                if (cameraFront) tr(R.string.camera_demo_capturing_front) else tr(R.string.camera_demo_capturing_back),
                color = Palette.onSurfaceVariant, fontSize = 15.sp, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(if (cameraFront) "📸" else "📷", fontSize = 44.sp)
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Palette.accent)
        }
        Overlay.MIC -> CenterOverlay {
            Text("🎙️", fontSize = 44.sp)
            Spacer(Modifier.height(12.dp))
            Text(formatTime(micTick), color = Palette.onBg, fontSize = 48.sp, fontWeight = FontWeight.Thin)
            Text("/ ${formatTime(MIC_SECONDS)}", color = Palette.muted, fontSize = 14.sp)
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Palette.surfaceVariant),
            ) {
                Box(
                    Modifier.fillMaxWidth(micTick.toFloat() / MIC_SECONDS).height(4.dp)
                        .clip(RoundedCornerShape(2.dp)).background(Palette.red),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(tr(R.string.microphone_demo_recording), color = Palette.maliciousText, fontSize = 14.sp)
        }
        Overlay.LOADING -> CenterOverlay {
            CircularProgressIndicator(color = Palette.accent)
            Spacer(Modifier.height(16.dp))
            Text(tr(loadingRes), color = Palette.onSurfaceVariant, fontSize = 15.sp)
        }
        null -> {}
    }
}

@Composable
private fun CenterOverlay(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xE6000000)).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Palette.surface)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
            )
        }
    }
}

@Composable
private fun CalcDisplay(value: String, operator: Char?) {
    val fontSize = when {
        value.length > 9 -> 32
        value.length > 6 -> 44
        else -> 56
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            when (operator) {
                '+' -> "+"; '-' -> "−"; '*' -> "×"; '/' -> "÷"; else -> " "
            },
            color = Palette.accent, fontSize = 22.sp,
        )
        Text(value, color = Palette.onBg, fontSize = fontSize.sp, fontWeight = FontWeight.Thin, maxLines = 1)
    }
}

@Composable
private fun Keypad(calc: CalculatorState) {
    val op = calc.operator
    Column(modifier = Modifier.padding(8.dp)) {
        Row {
            CalcKey("AC", Modifier.weight(1f), Palette.special, Palette.red) { calc.clear() }
            CalcKey("+/−", Modifier.weight(1f), Palette.special, Palette.accent) { calc.toggleSign() }
            CalcKey("%", Modifier.weight(1f), Palette.special, Palette.accent) { calc.percentage() }
            OpKey("÷", '/', op, Modifier.weight(1f)) { calc.handleOperator('/') }
        }
        Row {
            CalcKey("7", Modifier.weight(1f), Palette.digit) { calc.inputDigit('7') }
            CalcKey("8", Modifier.weight(1f), Palette.digit) { calc.inputDigit('8') }
            CalcKey("9", Modifier.weight(1f), Palette.digit) { calc.inputDigit('9') }
            OpKey("×", '*', op, Modifier.weight(1f)) { calc.handleOperator('*') }
        }
        Row {
            CalcKey("4", Modifier.weight(1f), Palette.digit) { calc.inputDigit('4') }
            CalcKey("5", Modifier.weight(1f), Palette.digit) { calc.inputDigit('5') }
            CalcKey("6", Modifier.weight(1f), Palette.digit) { calc.inputDigit('6') }
            OpKey("−", '-', op, Modifier.weight(1f)) { calc.handleOperator('-') }
        }
        Row {
            CalcKey("1", Modifier.weight(1f), Palette.digit) { calc.inputDigit('1') }
            CalcKey("2", Modifier.weight(1f), Palette.digit) { calc.inputDigit('2') }
            CalcKey("3", Modifier.weight(1f), Palette.digit) { calc.inputDigit('3') }
            OpKey("+", '+', op, Modifier.weight(1f)) { calc.handleOperator('+') }
        }
        Row {
            CalcKey("0", Modifier.weight(2f), Palette.digit) { calc.inputDigit('0') }
            CalcKey(".", Modifier.weight(1f), Palette.digit) { calc.inputDecimal() }
            CalcKey("=", Modifier.weight(1f), Palette.equals, Color.White) { calc.equals() }
        }
    }
}

@Composable
private fun OpKey(label: String, opChar: Char, active: Char?, modifier: Modifier, onClick: () -> Unit) {
    val isActive = active == opChar
    CalcKey(
        label = label,
        modifier = modifier,
        bg = if (isActive) Palette.accent else Palette.operator,
        textColor = if (isActive) Palette.bg else Palette.accent,
        onClick = onClick,
    )
}

@Composable
private fun CalcKey(
    label: String,
    modifier: Modifier,
    bg: Color,
    textColor: Color = Palette.onBg,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .height(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PermButton(icon: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Palette.permButtonBorder, RoundedCornerShape(10.dp))
            .background(Palette.permButton)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(label.uppercase(), color = Palette.permButtonText, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}
