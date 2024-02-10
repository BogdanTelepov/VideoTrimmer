package com.videotrimmer

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.InputStream
import java.util.Locale


fun Context.getMimeType(uri: Uri): String? {
    var mimeType: String? = null
    mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr = this.contentResolver
        cr.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri
                .toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }
    return mimeType
}


fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    } finally {
        inputStream?.close()
    }
}

fun scaleBitmap(bitmap: Bitmap, targetWidthDp: Float, targetHeightDp: Float): Bitmap {
    val density = Resources.getSystem().displayMetrics.density
    val targetWidthPx = (targetWidthDp * density).toInt()
    val targetHeightPx = (targetHeightDp * density).toInt()

    val width = bitmap.width
    val height = bitmap.height

    val scaleWidth = targetWidthPx.toFloat() / width
    val scaleHeight = targetHeightPx.toFloat() / height

    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)

    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
}