package com.videotrimmer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.videotrimmer.databinding.ItemViewPagerImagePreviewBinding

class ListImagesAdapter : RecyclerView.Adapter<ListImagesAdapter.ImageItemVH>() {

    private val items = mutableListOf<BaseCell.FilePreview>()

    var onRemoveClick: ((BaseCell.FilePreview) -> Unit)? = null

    fun addItems(list: List<BaseCell.FilePreview>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun removeItem(item: BaseCell.FilePreview) {
        items.remove(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemVH {
        return ImageItemVH(
            ItemViewPagerImagePreviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageItemVH, position: Int) {
        val currentItem = items[position]
        holder.onBind(item = currentItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ImageItemVH(private val binding: ItemViewPagerImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun onBind(item: BaseCell.FilePreview) = with(binding) {
            ivImage.setImageBitmap(item.bitmap)
            ivRemove.setOnClickListener {
                onRemoveClick?.invoke(item)
            }
        }
    }
}