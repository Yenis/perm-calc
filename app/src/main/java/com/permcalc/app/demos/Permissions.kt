package com.permcalc.app.demos

import android.Manifest
import android.os.Build

/** Runtime permissions to request for a given demo, adjusted for the OS version. */
fun permissionsFor(demo: Demo): Array<String> = when (demo) {
    Demo.CAMERA -> buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= 28) add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }.toTypedArray()

    Demo.MICROPHONE -> arrayOf(Manifest.permission.RECORD_AUDIO)

    Demo.CONTACTS -> arrayOf(Manifest.permission.READ_CONTACTS)

    Demo.LOCATION -> arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    Demo.STORAGE -> if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

/** Whether the granted-permission map satisfies what the demo needs to run. */
fun isGranted(demo: Demo, result: Map<String, Boolean>): Boolean = when (demo) {
    Demo.CAMERA -> result[Manifest.permission.CAMERA] == true
    Demo.MICROPHONE -> result[Manifest.permission.RECORD_AUDIO] == true
    Demo.CONTACTS -> result[Manifest.permission.READ_CONTACTS] == true
    Demo.LOCATION ->
        result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    Demo.STORAGE -> if (Build.VERSION.SDK_INT >= 33) {
        result[Manifest.permission.READ_MEDIA_IMAGES] == true ||
            result[Manifest.permission.READ_MEDIA_VIDEO] == true
    } else {
        result[Manifest.permission.READ_EXTERNAL_STORAGE] == true
    }
}
