package com.videotrimmer

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class BaseCell : Parcelable {
    object AddImage : BaseCell()
    data class FilePreview(
        var uri: Uri,
        var fileType: String?,
        var bitmap: Bitmap?,
        var isSelected: Boolean = true,
        val mediaType: MediaType
    ) : BaseCell()
}
