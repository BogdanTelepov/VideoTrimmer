package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kg.dev.videoeditor.databinding.ItemVideoThumbnailBinding

class VideoThumbNailAdapter : ListAdapter<Bitmap, VideoThumbnailViewHolder>(BitmapDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoThumbnailViewHolder {
        return VideoThumbnailViewHolder(
            ItemVideoThumbnailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoThumbnailViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}