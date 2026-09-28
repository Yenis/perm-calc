package com.gemstech.permcalc.demos

/**
 * Identifiers for the demos. The first five are runtime permissions; CLIPBOARD
 * is the odd one out - Android has no permission for it at all, which is the
 * whole point of that demo.
 */
enum class Demo(val key: String) {
    CAMERA("camera"),
    MICROPHONE("microphone"),
    CONTACTS("contacts"),
    LOCATION("location"),
    STORAGE("storage"),
    CLIPBOARD("clipboard");

    companion object {
        val ALL = listOf(CAMERA, MICROPHONE, CONTACTS, LOCATION, STORAGE, CLIPBOARD)

        /** The demos gated behind a real permission dialog. */
        val PERMISSION_DEMOS = listOf(CAMERA, MICROPHONE, CONTACTS, LOCATION, STORAGE)
    }
}

/** Result of a completed demo, shown in the reveal sheet. */
sealed interface DemoResult

data class CameraResult(val frontPath: String?, val backPath: String?) : DemoResult

data class MicResult(val filePath: String, val duration: String) : DemoResult

data class ContactItem(val name: String, val phone: String?, val email: String?)
data class ContactsResult(val contacts: List<ContactItem>) : DemoResult

data class AddressInfo(
    val fullLine: String?,
    val street: String?,
    val neighborhood: String?,
    val city: String?,
    val postalCode: String?,
    val district: String?,
    val region: String?,
    val country: String?,
)

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val altitude: Double?,
    val provider: String?,
    val timeMillis: Long?,
    val address: AddressInfo?,
) : DemoResult

data class MediaItem(val filename: String, val isVideo: Boolean, val dateMillis: Long?)
data class StorageResult(val items: List<MediaItem>, val count: Int) : DemoResult

/**
 * One read of the clipboard. Held in memory only, by the clipboard screen, and
 * dropped when that screen closes - never written anywhere.
 */
data class ClipboardResult(
    val items: List<ClipPiece>,
    /** Label the copying app attached to the clip, if any. */
    val label: String?,
    val mimeTypes: List<String>,
    /** When the clip was copied (wall-clock), or null if the system did not say. */
    val copiedAtMillis: Long?,
    /** The copying app flagged it as sensitive (password managers do). */
    val markedSensitive: Boolean,
    val findings: List<ClipFinding>,
)

sealed interface ClipPiece
data class ClipText(val text: String, val totalLength: Int) : ClipPiece
data class ClipImage(val bitmap: android.graphics.Bitmap, val mimeType: String?) : ClipPiece
data class ClipFile(val uri: String, val mimeType: String?, val name: String?) : ClipPiece

enum class FindingKind { PASSWORD, OTP, CARD, IBAN, CRYPTO, EMAIL, PHONE, URL }

/** Something in the clipped text that a malicious app's scanner would pick out. */
data class ClipFinding(val kind: FindingKind, val value: String)
