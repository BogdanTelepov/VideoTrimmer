package com.videotrimmer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canhub.cropper.CropImageView
import com.videotrimmer.databinding.FragmentImagePreviewBinding


class ImagePreviewFragment : Fragment(R.layout.fragment_image_preview) {
    companion object {
        private const val EXTRA_LIST_ITEMS = "EXTRA_LIST_ITEMS"
        private const val MAX_ITEMS = 5
        fun create(list: List<BaseCell.FilePreview>) =
            ImagePreviewFragment().withArgs(EXTRA_LIST_ITEMS to list)
    }

    private val listItems: List<BaseCell.FilePreview> by args(EXTRA_LIST_ITEMS)

    private val mutableSetItems: ArrayList<BaseCell.FilePreview> = ArrayList()

    private val binding: FragmentImagePreviewBinding by viewBinding()
    private var filePreview: BaseCell.FilePreview? = null
    private var isResize = false

    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val imageAdapter by lazy {
        ImageAdapter {
            pickImage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mutableSetItems.addAll(listItems)
        setupImagePicker(MAX_ITEMS)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rvListImages.apply {
            adapter = imageAdapter
            addSpaceDecorator(horizontalSpace = dip(8))
        }
        imageAdapter.setItems(mutableSetItems)
        selectItem(0)
        imageAdapter.onImageClick = {
            filePreview = it
            binding.cropImageView.setImageBitmap(it.bitmap)
        }
        binding.ivResize.setOnClickListener {
            isResize = !isResize
            if (isResize) {
                binding.cropImageView.scaleType = CropImageView.ScaleType.CENTER_CROP
                binding.cropImageView.resetCropRect()
            } else {
                binding.cropImageView.scaleType = CropImageView.ScaleType.FIT_CENTER
                binding.cropImageView.resetCropRect()
            }
        }
        binding.ivRemove.setOnClickListener {
//            Log.e("List items ->", mutableSetItems.size.toString())
//
//            val removeItem = mutableSetItems[imageAdapter.selectedItemPosition]
//            Log.e("List items selected item pos  ->", imageAdapter.selectedItemPosition.toString())
//            Log.e("List items selected remove item ->", removeItem.toString())
//            mutableSetItems.remove(removeItem)
//            imageAdapter.removeItem(removeItem)
//            binding.cropImageView.clearImage()
//            selectItem(imageAdapter.selectedItemPosition)
        }

        binding.toolbarNext.setOnClickListener {
            val bundle = Bundle()
            val json = toJson(mutableSetItems)
            bundle.putString("LIST", json)
            requireActivity().supportFragmentManager.setFragmentResult("EXTRA_IMAGES_LIST", bundle)
            requireActivity().supportFragmentManager.popBackStack()
            //  binding.cropImageView.croppedImageAsync()
        }
        binding.cropImageView.setOnCropImageCompleteListener { _, result ->
            if (result.isSuccessful) {
                val tempFile = mutableSetItems.find {
                    it.uri == filePreview?.uri
                }
                tempFile?.bitmap = result.bitmap
                tempFile?.uri = result.uriContent!!
                binding.cropImageView.setImageBitmap(result.bitmap)
                Log.e("Cropped image", result.getUriFilePath(requireContext(), true).toString())
            }
        }


    }

    private fun selectItem(position: Int) {
        if (position != imageAdapter.selectedItemPosition) {
            val previousSelectedItem = imageAdapter.selectedItemPosition
            imageAdapter.selectedItemPosition = position
            imageAdapter.notifyItemChanged(previousSelectedItem)
            imageAdapter.notifyItemChanged(imageAdapter.selectedItemPosition)
            val selectedDataItem = imageAdapter.getItemApPosition(imageAdapter.selectedItemPosition)
            binding.cropImageView.setImageBitmap(selectedDataItem.bitmap)


        }
    }

    private fun setupImagePicker(maxItems: Int) {
        pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems)) { uris ->
                if (uris.isNotEmpty()) {
                    uris.forEach {
                        val bitmap = getBitmapFromUri(requireContext(), it)
                        val fileType = requireContext().getMimeType(it)
                        mutableSetItems.add(BaseCell.FilePreview(it, fileType, bitmap))
                        imageAdapter.addItem(BaseCell.FilePreview(it, fileType, bitmap))
                        Log.e("List items ->", mutableSetItems.size.toString())
                    }

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
    }

    private fun pickImage() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

}