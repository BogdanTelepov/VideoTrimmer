package com.videotrimmer

import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.videotrimmer.databinding.FragmentNewPostConfirmationBinding

class NewPostConfirmationFragment : Fragment(R.layout.fragment_new_post_confirmation) {
    companion object {
        fun create() = NewPostConfirmationFragment()
    }

    private val binding: FragmentNewPostConfirmationBinding by viewBinding()

}