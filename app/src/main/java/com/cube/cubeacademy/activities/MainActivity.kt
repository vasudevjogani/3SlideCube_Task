package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cube.cubeacademy.activities.CreateNominationActivity
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.di.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val nominationAdapter = NominationsRecyclerViewAdapter()

    @Inject
    lateinit var repository: Repository

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNominationAddedEvent() {
        callNominationList(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun setListeners() {
        binding.swRefresh.setOnRefreshListener {
            callNominationList(true)
        }

        binding.createButton.setOnClickListener {
            startActivity(Intent(this, CreateNominationActivity::class.java))
        }
    }

    private fun initialiseAdapter() {
        binding.nominationsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = nominationAdapter
        }
    }

    /**
     * This will get Nomination list from server populate adapter with data.
     */
    private fun callNominationList(isRefresh:Boolean) {
        if (!isRefresh) {
            showLoading(isShowLoading = true, isShowError = false)
        }
        lifecycleScope.launch {
            val nominationList = repository.getAllNominations()

            if (nominationList.isNotEmpty()) {
                val nomineeList = repository.getAllNominees()
                nominationAdapter.nomineeList = nomineeList

                launch(Dispatchers.Main) {
                    nominationAdapter.submitList(nominationList)
                    showLoading(isShowLoading = false, isShowError = false)
                }
            } else {
                showLoading(isShowLoading = false, isShowError = true)
            }
            binding.swRefresh.isRefreshing = false
        }
    }

    private fun showLoading(isShowLoading:Boolean, isShowError:Boolean) {
        binding.nominationsList.visibility = if (isShowLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isShowLoading) View.VISIBLE else View.GONE

        if (isShowError){
            binding.emptyContainer.visibility = View.VISIBLE
            binding.swRefresh.visibility = View.GONE
        } else {
            binding.swRefresh.visibility = View.VISIBLE
        }
    }
}