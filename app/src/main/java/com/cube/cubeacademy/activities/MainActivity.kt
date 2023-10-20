package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.eventbus.NominationAdded
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val nominationAdapter = NominationsRecyclerViewAdapter()

    @Inject
    lateinit var repository: Repository

    /**
     * Used to refresh nomination list when new nomination added
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNominationAddedEvent(nominationAdded: NominationAdded) {
        callNominationList(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)

        populateUI()
    }

    private fun populateUI() {
        /**
         * TODO: Populate the UI with data in this function
         * 		 You need to fetch the list of user's nominations from the api and put the data in the recycler view
         * 		 And also add action to the "Create new nomination" button to go to the CreateNominationActivity
         */

        setListeners()
        initialiseAdapter()
        callNominationList(false)
    }

    /**
     * Used to set listener on UI elements
     */
    private fun setListeners() {
        binding.swRefresh.setOnRefreshListener {
            callNominationList(true)
        }

        binding.createButton.setOnClickListener {
            startActivity(Intent(this, CreateNominationActivity::class.java))
        }
    }

    /**
     * Used to set adapter to recyclerview
     */
    private fun initialiseAdapter() {
        binding.nominationsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = nominationAdapter
        }
    }

    /**
     * This will get Nomination list from server populate adapter with data.
     * And get Nominee List from server for mapping the name.
     */
    private fun callNominationList(isRefresh:Boolean) {
        if (!isRefresh) {
            showLoading(isShowLoading = true, isShowError = false)
        }
        lifecycleScope.launch {
            val nominationList = repository.getAllNominations()

            if (nominationList.isNotEmpty()) {
                val nomineeList = repository.getAllNominees()

                if (nomineeList.isNotEmpty()) {
                    nominationAdapter.nomineeList = nomineeList

                    launch(Dispatchers.Main) {
                        nominationAdapter.submitList(nominationList)
                        showLoading(isShowLoading = false, isShowError = false)
                    }
                } else {
                    showLoading(isShowLoading = false, isShowError = true)
                    Toast.makeText(this@MainActivity,getString(R.string.something_went_wrong_please_try_again_later), Toast.LENGTH_LONG).show()
                }
            } else {
                showLoading(isShowLoading = false, isShowError = true)
            }
            binding.swRefresh.isRefreshing = false
        }
    }

    /**
     * Show progress loader or error view based on api status
     */
    private fun showLoading(isShowLoading:Boolean, isShowError:Boolean) {
        binding.nominationsList.visibility = if (!isShowLoading && !isShowError) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isShowLoading) View.VISIBLE else View.GONE
        binding.emptyContainer.visibility = if (isShowError) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}