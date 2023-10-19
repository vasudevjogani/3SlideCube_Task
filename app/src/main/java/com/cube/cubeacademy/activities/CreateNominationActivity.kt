package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.lib.adapters.NomineeAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.eventbus.NominationAdded
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utility.dialog.ConfirmationBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNominationBinding
    private var selectedNomineeId: String = ""
    private var isProcess: Boolean = false
    private var processText: String = ""

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateUI()
    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */

        setListeners()
        callNomineeList()
    }

    private fun setListeners() {
        binding.constraintVeryUnfair.setOnClickListener {
            manageFeedbackButtons(it as ConstraintLayout, getString(R.string.very_unfair_value))
        }

        binding.constraintUnfair.setOnClickListener {
            manageFeedbackButtons(it as ConstraintLayout, getString(R.string.unfair_value))
        }

        binding.constraintNotSure.setOnClickListener {
            manageFeedbackButtons(it as ConstraintLayout, getString(R.string.not_sure_value))
        }

        binding.constraintFair.setOnClickListener {
            manageFeedbackButtons(it as ConstraintLayout, getString(R.string.fair_value))
        }

        binding.constraintVeryFair.setOnClickListener {
            manageFeedbackButtons(it as ConstraintLayout, getString(R.string.very_fair_value))
        }

        binding.spNominee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, l: Long) {
                val item = parent.getItemAtPosition(position)
                if ((item as Nominee).nomineeId.isNotEmpty()) {
                    selectedNomineeId = item.nomineeId
                } else {
                    selectedNomineeId = ""
                }
                updateSubmitButton()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.etReasoning.doAfterTextChanged {
            updateSubmitButton()
        }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                callNominationSubmit()
            }
        }

        binding.backButton.setOnClickListener {
            val bottomSheet = ConfirmationBottomSheetDialogFragment()
            bottomSheet.clickListener = {
                if (it){
                    finish()
                } else {
                    // Cancel
                }
            }
            bottomSheet.show(supportFragmentManager, ConfirmationBottomSheetDialogFragment::class.java.simpleName)
        }
    }

    private fun callNomineeList() {
        lifecycleScope.launch {
            val nomineeList = repository.getAllNominees()
            launch(Dispatchers.Main) {
                val nominee = Nominee("", getString(R.string.select_option), "")
                nomineeList.add(0, nominee)
                val nomineeAdapter = NomineeAdapter(this@CreateNominationActivity, R.layout.view_nominee_list_item, nomineeList)
                binding.spNominee.adapter = nomineeAdapter
            }
        }
    }

    private fun callNominationSubmit() {
        lifecycleScope.launch {
            val nomination = repository.createNomination(selectedNomineeId, binding.etReasoning.text.toString(), processText)

            launch(Dispatchers.Main) {
                EventBus.getDefault().post(NominationAdded())
                startActivity(Intent(this@CreateNominationActivity, NominationSubmittedActivity::class.java))
                finish()
            }
        }
    }

    private fun manageFeedbackButtons(view: ConstraintLayout, process: String) {
        binding.constraintVeryUnfair.isSelected = view == binding.constraintVeryUnfair
        binding.radioVeryUnfair.isSelected = view == binding.constraintVeryUnfair
        binding.constraintUnfair.isSelected = view == binding.constraintUnfair
        binding.radioUnfair.isSelected = view == binding.constraintUnfair
        binding.constraintNotSure.isSelected = view == binding.constraintNotSure
        binding.radioNotSure.isSelected = view == binding.constraintNotSure
        binding.constraintFair.isSelected = view == binding.constraintFair
        binding.radioFair.isSelected = view == binding.constraintFair
        binding.constraintVeryFair.isSelected = view == binding.constraintVeryFair
        binding.radioVeryFair.isSelected = view == binding.constraintVeryFair
        isProcess = true
        processText = process
        updateSubmitButton()
    }

    private fun updateSubmitButton() {
        binding.submitButton.isEnabled = selectedNomineeId.isNotEmpty() && !binding.etReasoning.text.isNullOrEmpty() && isProcess
    }
}