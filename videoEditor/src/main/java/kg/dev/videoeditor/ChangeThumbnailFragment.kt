package kg.dev.videoeditor

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kg.dev.videoeditor.adapter.VideoCoverAdapter
import kg.dev.videoeditor.databinding.FragmentChangeThumbnailBinding
import kg.dev.videoeditor.extensions.args
import kg.dev.videoeditor.extensions.withArgs
import kotlinx.coroutines.launch


class ChangeThumbnailFragment : Fragment(R.layout.fragment_change_thumbnail) {

    companion object {
        private const val EXTRA_FILE = "EXTRA_FILE"
        fun create(file: Uri) = ChangeThumbnailFragment().withArgs(EXTRA_FILE to file)
    }


    private val file: Uri by args(EXTRA_FILE)
    private val retriever = MediaMetadataRetriever()


    private val binding: FragmentChangeThumbnailBinding by viewBinding()
    private val vm: ChangeThumbnailViewModel by viewModels()

    private val coverAdapter by lazy {
        VideoCoverAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.loadThumbNails(retriever, file, requireContext())
        with(binding) {
            rvCovers.adapter = coverAdapter
            lifecycleScope.launch {
                vm.bitmap.collect {
                    if (it != null) {
                        coverAdapter.add(it)
                    }
                }
            }
            coverAdapter.onItemClick = {
                ivImage.setImageBitmap(it)
            }

        }

    }


}