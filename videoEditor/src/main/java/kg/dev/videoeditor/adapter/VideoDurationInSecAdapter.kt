package kg.dev.videoeditor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.dev.videoeditor.databinding.ItemVideoStepBinding

class VideoDurationInSecAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoStepInSecViewHolder(
            ItemVideoStepBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoStepInSecViewHolder -> {
                val item = data[position]
                holder.onBind(item)
            }


        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun setItems(intList: List<Int>) {
        data.clear()
        data.addAll(intList)
        notifyDataSetChanged()
    }


}