package com.videotrimmer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.videotrimmer.databinding.FragmentRootBinding
import kg.dev.videoeditor.VideoEditorFragment


class RootFragment : Fragment(R.layout.fragment_root) {


    companion object {
        fun create() = RootFragment()
    }

    private val binding: FragmentRootBinding by viewBinding()

    private var takeOrSelectVideoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            val data = result.data
            if (data!!.data != null) {
                Log.d("MainActivity ->", "Video path:: " + data.data)
                replace(VideoEditorFragment.create(data.data))
                //   openTrimActivity(data.data.toString())
            } else {
                Toast.makeText(requireContext(), "video uri is null", Toast.LENGTH_SHORT).show()
            }
        } else Log.d("MainActivity ->", "takeVideoResultLauncher data is null")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDefaultTrim.setOnClickListener {
            if (checkCamStoragePer()) openVideo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionOk(*grantResults)) {
            openVideo()
        }
    }

    private fun openVideo() {
        try {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        } catch (e: Exception) {
            e.printStackTrace()
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
                requireActivity(),
                permission
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