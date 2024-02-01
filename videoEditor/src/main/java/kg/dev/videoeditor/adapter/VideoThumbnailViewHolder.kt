package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoThumbnailBinding
import kg.dev.videoeditor.extensions.dip

class VideoThumbnailViewHolder(private val binding: ItemVideoThumbnailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val itemContext = binding.root.context

    fun onBind(bitmap: Bitmap) = with(binding) {
        val screenWidth =
            getScreenWidth(48)
        val itemWidth = (screenWidth / bindingAdapter?.itemCount!!).toInt()
        val bitmapNew = Bitmap.createScaledBitmap(
            bitmap,
            itemContext.dip(itemWidth),
            itemContext.dip(60),
            false
        )
        ivCover.setImageBitmap(bitmapNew)
    }

    private fun getScreenWidth(itemWidth: Int): Float {
        val widthDp = itemContext.resources.displayMetrics.run { widthPixels / density }
        return widthDp - itemWidth

    }
}