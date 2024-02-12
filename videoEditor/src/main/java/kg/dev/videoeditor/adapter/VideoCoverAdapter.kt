package kg.dev.videoeditor.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoCoverBinding
import kg.dev.videoeditor.utils.UnitConverter

class VideoCoverAdapter : RecyclerView.Adapter<VideoCoverAdapter.VideoCoverVH>() {

    companion object {
        private val WIDTH = UnitConverter.dpToPx(61)
        private val HEIGHT = UnitConverter.dpToPx(60)
    }

    private val items = mutableListOf<Bitmap>()

    var onItemClick: ((Bitmap) -> Unit)? = null


    fun add(bitmap: Bitmap) {
        items.add(bitmap)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCoverVH {
        return VideoCoverVH(
            ItemVideoCoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoCoverVH, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class VideoCoverVH(private val binding: ItemVideoCoverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(bitmap: Bitmap) = with(binding) {
            val bitmapNew = Bitmap.createScaledBitmap(
                bitmap, WIDTH,
                HEIGHT, false
            )
            ivCover.setImageBitmap(bitmapNew)
            itemView.setOnClickListener {
                onItemClick?.invoke(bitmap)
            }
        }
    }
}