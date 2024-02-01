package kg.dev.videoeditor.adapter

import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoStepBinding
import kg.dev.videoeditor.extensions.dip

class VideoStepInSecViewHolder(private val binding: ItemVideoStepBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val itemContext = binding.root.context

    fun onBind(value: Int) = with(binding) {
        val itemCount = bindingAdapter?.itemCount ?: 0

        val screenWidth: Float = if (itemCount > 10) {
            getScreenWidth(72)
        } else {
            getScreenWidth(64)
        }


        val itemWidth = (screenWidth / itemCount).toInt()


        tvTitle.layoutParams.width = itemContext.dip(itemWidth)
        tvTitle.text = "${value}c"
    }

    private fun getScreenWidth(itemWidth: Int): Float {
        val widthDp = itemContext.resources.displayMetrics.run { widthPixels / density }
        return widthDp - itemWidth

    }

}