package com.videotrimmer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.reflect.TypeToken
import com.videotrimmer.databinding.FragmentNewPostBinding


class NewPostFragment : Fragment(R.layout.fragment_new_post) {
    companion object {
        fun create() = NewPostFragment()
    }

    private val binding: FragmentNewPostBinding by viewBinding()

    private var listBitmap = mutableListOf<BaseCell.FilePreview>()

    private val listImagesAdapter by lazy {
        ListImagesAdapter()
    }

    private val images = ArrayList<BaseCell.FilePreview>()


    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                uris.forEach {
                    val bitmap = getBitmapFromUri(requireContext(), it)
                    val fileType = requireContext().getMimeType(it)
                    val mediaType = requireContext().getMediaType(it)
                    listBitmap.add(BaseCell.FilePreview(it, fileType, bitmap, false, mediaType))
                    Log.d("PhotoPicker", "Uri path: ${it.path}")
                }
                replace(ImagePreviewFragment.create(listBitmap))
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            etInputText.focusAndShowKeyboard()
            ivAddImage.setOnClickListener {
                if (checkCamStoragePer()) pickImage()
            }
            rvListImages.apply {
                adapter = listImagesAdapter
                addSpaceDecorator(horizontalSpace = dip(16))
            }
            listImagesAdapter.onRemoveClick = {
                images.remove(it)
                listImagesAdapter.removeItem(it)
                rvListImages.isVisible = images.isNotEmpty()
            }
            ivClose.setOnClickListener {
                CommonBottomSheet.showDialog(this@NewPostFragment) {

                }
            }
            progressBarLayout.progressBar.max = 100

            etInputText.doOnTextChanged { text, _, _, _ ->
                val textSize = text?.length ?: 0
                progressBarLayout.progressBar.progress = textSize
                progressBarLayout.tvProgress.text = "${100 - textSize}"
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "EXTRA_IMAGES_LIST", viewLifecycleOwner
        ) { requestKey, bundle ->
            val result = bundle.getString("LIST")
            val type = object : TypeToken<ArrayList<BaseCell.FilePreview>>() {}.type
            val data = result?.let { parseArray<ArrayList<BaseCell.FilePreview>>(it, type) }
            if (data != null) {
                images.addAll(data)
                listImagesAdapter.addItems(data)
                binding.rvListImages.isVisible = images.isNotEmpty()
            }
            Log.d("JSON", data.toString())

        }

    }

    private fun pickImage() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionOk(*grantResults)) {
            pickImage()
        }
    }


    private fun checkCamStoragePer(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(
                Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
        } else checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        )
    }

    private fun checkPermission(vararg permissions: String): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(
                requireActivity(), permission
            ) == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        if (allPermitted) return true
        ActivityCompat.requestPermissions(
            requireActivity(), permissions, 220
        )
        return false
    }


    private fun isPermissionOk(vararg results: Int): Boolean {
        var isAllGranted = true
        for (result in results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false
                break
            }
        }
        return isAllGranted
    }


}