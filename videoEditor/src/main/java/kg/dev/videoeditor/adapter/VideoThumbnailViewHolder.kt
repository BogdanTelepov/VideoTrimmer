package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoThumbnailBinding
import kg.dev.videoeditor.utils.VideoTrimUtils.THUMB_HEIGHT
import kg.dev.videoeditor.utils.VideoTrimUtils.VIDEO_FRAMES_WIDTH

class VideoThumbnailViewHolder(private val binding: ItemVideoThumbnailBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun onBind(bitmap: Bitmap) = with(binding) {
        val itemCount = bindingAdapter?.itemCount ?: 0
        val itemWidth = VIDEO_FRAMES_WIDTH / itemCount
        val bitmapNew = Bitmap.createScaledBitmap(bitmap, itemWidth, THUMB_HEIGHT, false)
        ivCover.setImageBitmap(bitmapNew)
    }


}