package kg.dev.videoeditor.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri


fun Context.getDuration(videoPath: Uri): Long {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, videoPath)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMilliSec = time?.toLong()
        retriever.release()
        return timeInMilliSec?.div(1000) ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

fun formatSeconds(timeInSeconds: Long): String? {
    val hours = timeInSeconds / 3600
    val secondsLeft = timeInSeconds - hours * 3600
    val minutes = secondsLeft / 60
    val seconds = secondsLeft - minutes * 60
    var formattedTime = ""
    if (hours < 10 && hours != 0L) {
        formattedTime += "0"
        formattedTime += "$hours:"
    }
    if (minutes < 10) formattedTime += "0"
    formattedTime += "$minutes:"
    if (seconds < 10) formattedTime += "0"
    formattedTime += seconds
    return formattedTime
}