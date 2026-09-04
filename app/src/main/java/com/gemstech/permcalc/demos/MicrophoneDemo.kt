package com.gemstech.permcalc.demos

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.delay
import java.io.File

const val MIC_SECONDS = 8

/**
 * Records a short fixed-length clip automatically (no Stop button) and saves it
 * to the app's storage, returning the path and formatted duration.
 */
suspend fun runMicrophoneDemo(context: Context, onTick: (Int) -> Unit): MicResult {
    val file = File(context.filesDir, "permcalc_demo.m4a")
    if (file.exists()) file.delete()

    @Suppress("DEPRECATION")
    val recorder = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context) else MediaRecorder()

    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)
        setOutputFile(file.absolutePath)
        prepare()
        start()
    }

    try {
        for (s in 1..MIC_SECONDS) {
            delay(1000)
            onTick(s)
        }
    } finally {
        try {
            recorder.stop()
        } catch (_: Exception) {
        }
        recorder.release()
    }

    return MicResult(file.absolutePath, formatTime(MIC_SECONDS))
}

fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}
