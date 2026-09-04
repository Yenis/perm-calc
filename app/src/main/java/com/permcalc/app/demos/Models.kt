package com.permcalc.app.demos

/** Identifiers for the five permission demos. */
enum class Demo(val key: String) {
    CAMERA("camera"),
    MICROPHONE("microphone"),
    CONTACTS("contacts"),
    LOCATION("location"),
    STORAGE("storage");

    companion object {
        val ALL = listOf(CAMERA, MICROPHONE, CONTACTS, LOCATION, STORAGE)
    }
}

/** Result of a completed demo, shown in the reveal sheet. */
sealed interface DemoResult

data class CameraResult(val frontPath: String?, val backPath: String?) : DemoResult

data class MicResult(val filePath: String, val duration: String) : DemoResult

data class ContactItem(val name: String, val phone: String?, val email: String?)
data class ContactsResult(val contacts: List<ContactItem>) : DemoResult

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val address: String?,
) : DemoResult

data class MediaItem(val filename: String, val isVideo: Boolean, val dateMillis: Long?)
data class StorageResult(val items: List<MediaItem>, val count: Int) : DemoResult
