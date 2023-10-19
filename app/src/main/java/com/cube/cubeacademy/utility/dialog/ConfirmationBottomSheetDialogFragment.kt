package com.cube.cubeacademy.utility.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cube.cubeacademy.databinding.FragmentConfirmationBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmationBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentConfirmationBottomSheetDialogBinding
    var clickListener: (isYesClicked: Boolean) -> Unit? = { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConfirmationBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
    }

    private fun populateUI() {
        binding.yesButton.setOnClickListener {
            dismiss()
            clickListener(true)
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
            clickListener(false)
        }
    }
}