package com.imageeditor

import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imageeditor.databinding.ItemImagePreviewBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImagePreviewVH>() {


    private val listItems = mutableListOf<Bitmap>()


    fun setItems(list: List<Bitmap>) {
        listItems.clear()
        listItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewVH {
        return ImagePreviewVH(
            ItemImagePreviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagePreviewVH, position: Int) {
        holder.onBind(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
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

        fun onBind(bitmap: Bitmap) = with(binding) {

            val bitmapNew = Bitmap.createScaledBitmap(
                bitmap, dpToPx(88), dpToPx(88), false
            )
            ivImage.setImageBitmap(bitmapNew)
            tvNumber.text = adapterPosition.toString()
        }
    }

}