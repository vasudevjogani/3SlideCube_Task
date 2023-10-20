package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cube.cubeacademy.databinding.ActivityNominationSubmittedBinding

class NominationSubmittedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNominationSubmittedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNominationSubmittedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateUI()
    }

    private fun populateUI() {
        /**
         * TODO: Add the logic for the two buttons (Don't forget that if you start to add a new nomination, the back button shouldn't come back here)
         */
        setListeners()
    }

    /**
     * Used to set listener on UI elements
     */
    private fun setListeners() {
        binding.backButton.setOnClickListener {
            /**
             * Only finishing this activity so previous activity (MainActivity) resume.
             */
            finish()
        }

        binding.submitButton.setOnClickListener {
            /**
             * Starting CreateNominationActivity and finishing this activity so that this activity not resume on back.
             */
            startActivity(Intent(this, CreateNominationActivity::class.java))
            finish()
        }
    }
}