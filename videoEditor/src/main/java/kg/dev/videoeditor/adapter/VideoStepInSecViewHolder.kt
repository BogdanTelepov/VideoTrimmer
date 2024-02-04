package kg.dev.videoeditor.adapter

import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoStepBinding
import kg.dev.videoeditor.utils.UnitConverter
import kg.dev.videoeditor.utils.VideoTrimUtils.ITEM_SECOND_WIDTH

class VideoStepInSecViewHolder(private val binding: ItemVideoStepBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun onBind(value: Int) = with(binding) {
        val itemCount = bindingAdapter?.itemCount ?: 0
        val spaceItemWidth = UnitConverter.dpToPx(1) * itemCount
        val itemWidth = (ITEM_SECOND_WIDTH - spaceItemWidth) / itemCount
        tvTitle.layoutParams.width = itemWidth
        tvTitle.text = "${value}c"
    }
}