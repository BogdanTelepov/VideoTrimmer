package com.videotrimmer

import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.videotrimmer.databinding.ItemAddImageBinding
import com.videotrimmer.databinding.ItemImagePreviewBinding
import kg.dev.videoeditor.utils.formatSeconds
import kg.dev.videoeditor.utils.getDuration

class ImageAdapter(private val addImage: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MAX_ITEMS = 5
    }


    private val listItems = mutableListOf<BaseCell>()


    var onImageClick: ((BaseCell.FilePreview) -> Unit)? = null

    var selectedItemPosition: Int = RecyclerView.NO_POSITION


    fun setItems(list: List<BaseCell>) {
        listItems.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(item: BaseCell) {
        listItems.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(item: BaseCell.FilePreview) {
        listItems.remove(item)
        notifyDataSetChanged()
    }

    fun getItemApPosition(position: Int): BaseCell.FilePreview {
        return listItems[position] as BaseCell.FilePreview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_image_preview -> ImagePreviewVH(
                ItemImagePreviewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.item_add_image -> AddImageVH(
                ItemAddImageBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> throw IllegalArgumentException("layout not found")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listItems.size) {
            R.layout.item_image_preview
        } else {
            R.layout.item_add_image
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImagePreviewVH -> {
                val item = listItems[position] as BaseCell.FilePreview
                holder.onBind(item, position)

            }

            is AddImageVH -> {
                holder.onBind { addImage.invoke() }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listItems.size < MAX_ITEMS) {
            listItems.size + 1
        } else {
            listItems.size
        }
    }

    inner class ImagePreviewVH(private val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val itemContext = binding.root.context

        private fun getDisplayMetrics(): DisplayMetrics {
            return itemContext.resources.displayMetrics;
        }

        private fun dpToPx(dp: Int): Int {
            return (dp * getDisplayMetrics().density + 0.5f).toInt()
        }

        fun onBind(bitmap: BaseCell.FilePreview, position: Int) = with(binding) {
            if (bitmap.mediaType == MediaType.MediaTypeVideo) {
                val duration = itemContext.getDuration(bitmap.uri)
                val formattedTime = formatSeconds(duration)
                tvDuration.visible()
                tvDuration.text = formattedTime

            }
            val scaledBitmap = bitmap.bitmap?.let { scaleBitmap(it, 88f, 88f) }
            ivImage.setImageBitmap(scaledBitmap)
            val formattedPosition = (bindingAdapterPosition + 1)
            tvNumber.text = "$formattedPosition"

            if (selectedItemPosition == position) {
                cardView.setStrokeColor(ColorStateList.valueOf(itemContext.getColorCompat(R.color.accent1)))

            } else {
                cardView.setStrokeColor(ColorStateList.valueOf(itemContext.getColorCompat(R.color.border)))
            }
            cardView.setOnClickListener {
                onItemClicked(position)
                onImageClick?.invoke(bitmap)
            }

        }


        private fun onItemClicked(position: Int) {
            if (selectedItemPosition != position) {
                val prevPosition = selectedItemPosition
                selectedItemPosition = position
                notifyItemChanged(prevPosition)
                notifyItemChanged(selectedItemPosition)
            }
        }

    }

    inner class AddImageVH(private val binding: ItemAddImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(block: () -> Unit) = with(binding) {
            rootLayout.setOnClickListener {
                block.invoke()
            }
        }
    }

}