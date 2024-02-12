package com.videotrimmer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.videotrimmer.databinding.FragmentCommonBottomSheetBinding

class CommonBottomSheet(private val block: () -> Unit) :
    BaseBottomSheetDialogFragment(R.layout.fragment_common_bottom_sheet) {

    companion object {

        fun showDialog(
            fragment: Fragment,
            block: () -> Unit = {}
        ) {
            CommonBottomSheet(block).show(
                fragment.childFragmentManager,
                CommonBottomSheet::javaClass.name
            )
        }
    }

    private lateinit var binding: FragmentCommonBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommonBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            block.invoke()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}