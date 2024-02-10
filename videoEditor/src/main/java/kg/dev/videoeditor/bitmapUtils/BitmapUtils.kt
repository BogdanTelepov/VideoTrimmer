package kg.dev.videoeditor.bitmapUtils

import android.graphics.Bitmap
import android.util.Log
import kotlin.math.max

enum class RequestSizeOptions {
    /** No resize/sampling is done unless required for memory management (OOM). */
    NONE,

    /**
     * Only sample the image during loading (if image set using URI) so the smallest of the image
     * dimensions will be between the requested size and x2 requested size.<br></br>
     * NOTE: resulting image will not be exactly requested width/height see: [Loading Large Bitmaps Efficiently](http://developer.android.com/training/displaying-bitmaps/load-bitmap.html).
     */
    SAMPLING,

    /**
     * Resize the image uniformly (maintain the image's aspect ratio) so that both dimensions (width
     * and height) of the image will be equal to or **less** than the corresponding requested
     * dimension.<br></br>
     * If the image is smaller than the requested size it will NOT change.
     */
    RESIZE_INSIDE,

    /**
     * Resize the image uniformly (maintain the image's aspect ratio) to fit in the given
     * width/height.<br></br>
     * The largest dimension will be equals to the requested and the second dimension will be
     * smaller.<br></br>
     * If the image is smaller than the requested size it will enlarge it.
     */
    RESIZE_FIT,

    /**
     * Resize the image to fit exactly in the given width/height.<br></br>
     * This resize method does NOT preserve aspect ratio.<br></br>
     * If the image is smaller than the requested size it will enlarge it.
     */
    RESIZE_EXACT,
}

fun resizeBitmap(
    bitmap: Bitmap?,
    reqWidth: Int,
    reqHeight: Int,
    options: RequestSizeOptions,
): Bitmap {
    try {
        if (reqWidth > 0 && reqHeight > 0 && (options === RequestSizeOptions.RESIZE_FIT || options === RequestSizeOptions.RESIZE_INSIDE || options === RequestSizeOptions.RESIZE_EXACT)) {
            var resized: Bitmap? = null
            if (options === RequestSizeOptions.RESIZE_EXACT) {
                resized = Bitmap.createScaledBitmap(bitmap!!, reqWidth, reqHeight, false)
            } else {
                val width = bitmap!!.width
                val height = bitmap.height
                val scale = max(width / reqWidth.toFloat(), height / reqHeight.toFloat())
                if (scale > 1 || options === RequestSizeOptions.RESIZE_FIT) {
                    resized = Bitmap.createScaledBitmap(
                        bitmap,
                        (width / scale).toInt(),
                        (height / scale).toInt(),
                        false,
                    )
                }
            }
            if (resized != null) {
                if (resized != bitmap) {
                    bitmap.recycle()
                }
                return resized
            }
        }
    } catch (e: Exception) {
        Log.w("AIC", "Failed to resize cropped image, return bitmap before resize", e)
    }
    return bitmap!!
}