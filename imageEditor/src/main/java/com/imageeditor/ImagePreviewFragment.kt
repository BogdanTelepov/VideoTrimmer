package com.imageeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.imageeditor.databinding.FragmentImagePreviewBinding


class ImagePreviewFragment : Fragment(R.layout.fragment_image_preview) {
    companion object {
        private const val EXTRA_LIST_ITEMS = "EXTRA_LIST_ITEMS"
        fun create(list: List<Bitmap>) = ImagePreviewFragment().withArgs(EXTRA_LIST_ITEMS to list)
    }

    private val listItems: List<Bitmap> by args(EXTRA_LIST_ITEMS)

    private val binding: FragmentImagePreviewBinding by viewBinding()

    private val imageAdapter by lazy {
        ImageAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.rvListImages.adapter = imageAdapter
        imageAdapter.setItems(listItems)
    }

}