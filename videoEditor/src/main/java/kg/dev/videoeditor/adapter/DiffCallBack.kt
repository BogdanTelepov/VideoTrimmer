package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.DiffUtil

class DiffCallBack : DiffUtil.ItemCallback<Bitmap>() {
    override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean =
        oldItem.sameAs(newItem)
}