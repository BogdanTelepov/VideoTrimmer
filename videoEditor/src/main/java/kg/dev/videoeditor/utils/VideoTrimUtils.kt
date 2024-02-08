package kg.dev.videoeditor.utils

object VideoTrimUtils {

    val SCREEN_WIDTH_FULL: Int = getDeviceWidth()
    val RECYCLER_VIEW_PADDING: Int = UnitConverter.dpToPx(24)
    val ITEM_SECOND_PADDING: Int = UnitConverter.dpToPx(16)
    val VIDEO_FRAMES_WIDTH: Int = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2
    val ITEM_SECOND_WIDTH: Int = SCREEN_WIDTH_FULL - ITEM_SECOND_PADDING * 2
    val THUMB_HEIGHT = UnitConverter.dpToPx(60)
    const val MAX_CUT_DURATION = 60 * 1000L
    const val MAX_COUNT_RANGE = 60
    const val MIN_CUT_DURATION = 3 * 1000L
    val PADDING: Int = UnitConverter.dpToPx(30)
    val PADDING_RIGHT: Int = UnitConverter.dpToPx(11)


    fun getDeviceWidth(): Int {
        return BaseUtils.getContext().resources.displayMetrics.widthPixels
    }

    fun getDeviceHeight(): Int {
        return BaseUtils.getContext().resources.displayMetrics.heightPixels
    }

    fun roundToNearestThousand(input: Int): Int {
        val remainder = input % 1000
        val adjustment = if (remainder > 500) {
            1000 - remainder
        } else {
            -remainder
        }
        return input + adjustment
    }

    fun roundToNearestThousand(input: Long): Long {
        val remainder = input % 1000L
        val adjustment = if (remainder > 500L) {
            1000L - remainder
        } else {
            -remainder
        }
        return input + adjustment
    }

}