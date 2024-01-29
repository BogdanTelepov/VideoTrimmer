package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kg.dev.videoeditor.databinding.ItemVideoThumbnailBinding
import kg.dev.videoeditor.extensions.dip

class VideoThumbnailViewHolder(private val binding: ItemVideoThumbnailBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val itemContext = binding.root.context

    fun onBind(bitmap: Bitmap) = with(binding) {
        val screenWidth =
            getScreenWidth(48)
        val itemWidth = screenWidth / 8
        val layoutParams = itemView.layoutParams
        layoutParams.width = itemWidth
        layoutParams.height = itemContext.dip(60)
        itemView.layoutParams = layoutParams
        Glide.with(binding.root).load(bitmap).into(binding.ivCover)
    }

    private fun getScreenWidth(itemWidth: Int): Int {
        (itemContext.resources.displayMetrics).apply {
            return widthPixels - itemContext.dip(itemWidth)
        }
    }
}