package com.permcalc.app.ui

import com.permcalc.app.R
import com.permcalc.app.demos.Demo

/** Resource IDs backing a demo's info sheet. */
data class DemoInfoRes(
    val infoTitle: Int,
    val infoSubtitle: Int,
    val legitimateArray: Int,
    val maliciousArray: Int,
    val warning: Int,
)

fun demoInfoRes(demo: Demo): DemoInfoRes = when (demo) {
    Demo.CAMERA -> DemoInfoRes(
        R.string.camera_info_title, R.string.camera_info_subtitle,
        R.array.camera_legitimate, R.array.camera_malicious, R.string.camera_warning,
    )
    Demo.MICROPHONE -> DemoInfoRes(
        R.string.microphone_info_title, R.string.microphone_info_subtitle,
        R.array.microphone_legitimate, R.array.microphone_malicious, R.string.microphone_warning,
    )
    Demo.CONTACTS -> DemoInfoRes(
        R.string.contacts_info_title, R.string.contacts_info_subtitle,
        R.array.contacts_legitimate, R.array.contacts_malicious, R.string.contacts_warning,
    )
    Demo.LOCATION -> DemoInfoRes(
        R.string.location_info_title, R.string.location_info_subtitle,
        R.array.location_legitimate, R.array.location_malicious, R.string.location_warning,
    )
    Demo.STORAGE -> DemoInfoRes(
        R.string.storage_info_title, R.string.storage_info_subtitle,
        R.array.storage_legitimate, R.array.storage_malicious, R.string.storage_warning,
    )
}

fun permButtonLabelRes(demo: Demo): Int = when (demo) {
    Demo.CAMERA -> R.string.perm_camera
    Demo.MICROPHONE -> R.string.perm_microphone
    Demo.CONTACTS -> R.string.perm_contacts
    Demo.LOCATION -> R.string.perm_location
    Demo.STORAGE -> R.string.perm_storage
}

fun demoRunningRes(demo: Demo): Int = when (demo) {
    Demo.CAMERA -> R.string.camera_running
    Demo.MICROPHONE -> R.string.microphone_running
    Demo.CONTACTS -> R.string.contacts_running
    Demo.LOCATION -> R.string.location_running
    Demo.STORAGE -> R.string.storage_running
}

fun demoIcon(demo: Demo): String = when (demo) {
    Demo.CAMERA -> "📷"
    Demo.MICROPHONE -> "🎙️"
    Demo.CONTACTS -> "👥"
    Demo.LOCATION -> "📍"
    Demo.STORAGE -> "🗂️"
}
