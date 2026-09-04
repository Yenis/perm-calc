package com.permcalc.app.demos

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Silently captures a front then a rear photo using CameraX with no preview,
 * saves both to the gallery, and returns their local paths.
 *
 * Capture happens by itself: CameraX opens the camera when bound, and takePicture
 * fires as soon as the sensor is ready — no user tap, no shutter sound.
 */
suspend fun runCameraDemo(activity: ComponentActivity, onFront: (Boolean) -> Unit): CameraResult {
    val provider = ProcessCameraProvider.getInstance(activity).awaitFuture()

    onFront(true)
    val front = captureOne(activity, provider, CameraSelector.DEFAULT_FRONT_CAMERA)
    onFront(false)
    val back = captureOne(activity, provider, CameraSelector.DEFAULT_BACK_CAMERA)

    withContext(Dispatchers.Main) { provider.unbindAll() }

    front?.let { saveImageToGallery(activity, it) }
    back?.let { saveImageToGallery(activity, it) }

    return CameraResult(front?.absolutePath, back?.absolutePath)
}

private suspend fun captureOne(
    activity: ComponentActivity,
    provider: ProcessCameraProvider,
    selector: CameraSelector,
): File? = withContext(Dispatchers.Main) {
    if (!provider.hasCamera(selector)) return@withContext null

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    try {
        provider.unbindAll()
        provider.bindToLifecycle(activity, selector, imageCapture)
    } catch (e: Exception) {
        return@withContext null
    }

    // Brief settle so auto-exposure/focus produce a usable frame, then capture immediately.
    delay(500)

    val file = File(activity.cacheDir, "permcalc_${System.currentTimeMillis()}.jpg")
    val options = ImageCapture.OutputFileOptions.Builder(file).build()

    suspendCancellableCoroutine { cont ->
        imageCapture.takePicture(
            options,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cont.resume(file)
                }

                override fun onError(exc: ImageCaptureException) {
                    cont.resume(null)
                }
            },
        )
    }
}

/** Copies a captured file into the shared gallery (Pictures/PermCalc). */
private fun saveImageToGallery(context: Context, file: File) {
    try {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PermCalc")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return
            resolver.openOutputStream(uri)?.use { out -> file.inputStream().use { it.copyTo(out) } }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } else {
            @Suppress("DEPRECATION")
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PermCalc",
            )
            dir.mkdirs()
            val dest = File(dir, file.name)
            file.inputStream().use { input -> dest.outputStream().use { input.copyTo(it) } }
            android.media.MediaScannerConnection.scanFile(
                context, arrayOf(dest.absolutePath), arrayOf("image/jpeg"), null,
            )
        }
    } catch (_: Exception) {
        // Best-effort: the reveal still shows the cached photos even if gallery save fails.
    }
}

/** Awaits a ListenableFuture on the main executor. */
private suspend fun <T> ListenableFuture<T>.awaitFuture(): T =
    suspendCancellableCoroutine { cont ->
        addListener(
            {
                try {
                    cont.resume(get())
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            Runnable::run,
        )
    }
