package com.cube.cubeacademy.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.lib.adapters.NomineeAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.eventbus.NominationAdded
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResultWrapper
import com.cube.cubeacademy.utility.dialog.ConfirmationBottomSheetDialogFragment
import com.cube.cubeacademy.utility.extention.showErrorToast
import com.cube.cubeacademy.utility.extention.showNoInternetToast
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

        setBoldAndColorSpannable(binding.tvCubeOf, getString(R.string.cube_of_the_month))
    }

    /**
     * Used to set listener on UI elements
     */
    private fun setListeners() {
        binding.constraintVeryUnfair.setOnClickListener {
            updateFeedbackButtons(it as ConstraintLayout, getString(R.string.very_unfair_value))
        }

        binding.constraintUnfair.setOnClickListener {
            updateFeedbackButtons(it as ConstraintLayout, getString(R.string.unfair_value))
        }

        binding.constraintNotSure.setOnClickListener {
            updateFeedbackButtons(it as ConstraintLayout, getString(R.string.not_sure_value))
        }

        binding.constraintFair.setOnClickListener {
            updateFeedbackButtons(it as ConstraintLayout, getString(R.string.fair_value))
        }

        binding.constraintVeryFair.setOnClickListener {
            updateFeedbackButtons(it as ConstraintLayout, getString(R.string.very_fair_value))
        }

        binding.spNominee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, l: Long) {
                val item = parent.getItemAtPosition(position)
                selectedNomineeId = if ((item as Nominee).nomineeId.isNotEmpty()) {
                    item.nomineeId
                } else {
                    ""
                }
                updateSubmitButtonState()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.etReasoning.doAfterTextChanged {
            updateSubmitButtonState()
        }

        binding.submitButton.setOnClickListener {
            if (binding.submitButton.isEnabled) {
                callNominationSubmit()
            }
        }

        binding.backButton.setOnClickListener {
            val bottomSheet = ConfirmationBottomSheetDialogFragment()
            bottomSheet.clickListener = {
                if (it) {
                    finish()
                } else {
                    // Cancel
                }
            }
            bottomSheet.show(supportFragmentManager, ConfirmationBottomSheetDialogFragment::class.java.simpleName)
        }
    }

    /**
     * Get All nominees from api and populate spinner with adapter
     */
    private fun callNomineeList() {
        lifecycleScope.launch {
            when (val nomineeListResponse = repository.getAllNominees()) {
                is ResultWrapper.GenericError -> {
                    setSpinnerAdapter(arrayListOf())
                    showErrorToast()
                }

                is ResultWrapper.NetworkError -> {
                    setSpinnerAdapter(arrayListOf())
                    showNoInternetToast()
                }

                is ResultWrapper.Success -> {
                    val nomineeList = nomineeListResponse.value
                    if (nomineeList.isNotEmpty()) {
                        launch(Dispatchers.Main) {
                            setSpinnerAdapter(nomineeList)
                        }
                    }
                }
            }
        }
    }

    /**
     * Used to set adapter for spinner.
     */
    private fun setSpinnerAdapter(nomineeList: ArrayList<Nominee>) {
        val nominee = Nominee("", getString(R.string.select_option), "")
        nomineeList.add(0, nominee)
        val nomineeAdapter = NomineeAdapter(this@CreateNominationActivity, R.layout.view_nominee_list_item, nomineeList)
        binding.spNominee.adapter = nomineeAdapter
    }

    /**
     * Used to create nomination on server and send event to main activity so nomination list refresh automatically.
     * And redirect to NominationSubmittedActivity when create nomination is success.
     */
    private fun callNominationSubmit() {
        lifecycleScope.launch {

            when (val nominationResponse = repository.createNomination(selectedNomineeId, binding.etReasoning.text.toString(), processText)) {
                is ResultWrapper.GenericError -> showErrorToast()
                is ResultWrapper.NetworkError -> showNoInternetToast()
                is ResultWrapper.Success -> {
                    val nomination = nominationResponse.value
                    if (nomination != null) {
                        launch(Dispatchers.Main) {
                            EventBus.getDefault().post(NominationAdded())
                            startActivity(Intent(this@CreateNominationActivity, NominationSubmittedActivity::class.java))
                            finish()
                        }
                    } else {
                        showErrorToast()
                    }
                }
            }
        }
    }

    /**
     * Update feedback buttons UI state based on selection.
     */
    private fun updateFeedbackButtons(view: ConstraintLayout, process: String) {
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
        updateSubmitButtonState()
    }

    /**
     * Enable or Disable submit button based on form fields.
     * Enable when form is filled.
     * Disable when form is not filled.
     */
    private fun updateSubmitButtonState() {
        binding.submitButton.isEnabled = selectedNomineeId.isNotEmpty() && !binding.etReasoning.text.isNullOrEmpty() && isProcess
    }

    private fun setBoldAndColorSpannable(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.pink)), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.highlightColor = Color.TRANSPARENT
        }
        textView.text = spannableString
    }
}