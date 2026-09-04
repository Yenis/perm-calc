package com.gemstech.permcalc.demos

import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Scans the shared media store for photos and videos: filename, type, and date. */
suspend fun runStorageDemo(context: Context): StorageResult = withContext(Dispatchers.IO) {
    val collection = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
    )
    val sort = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    val items = ArrayList<MediaItem>()
    var total = 0

    context.contentResolver.query(collection, projection, selection, selectionArgs, sort)?.use { c ->
        total = c.count
        val nameIdx = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val dateIdx = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
        val typeIdx = c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
        while (c.moveToNext() && items.size < 30) {
            val name = c.getString(nameIdx) ?: continue
            val dateSecs = c.getLong(dateIdx)
            val isVideo = c.getInt(typeIdx) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
            items.add(
                MediaItem(
                    filename = name,
                    isVideo = isVideo,
                    dateMillis = if (dateSecs > 0) dateSecs * 1000 else null,
                ),
            )
        }
    }

    StorageResult(items, total)
}
