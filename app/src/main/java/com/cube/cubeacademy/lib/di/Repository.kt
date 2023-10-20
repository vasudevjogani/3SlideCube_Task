package com.cube.cubeacademy.lib.di

import android.widget.Toast
import com.cube.cubeacademy.MainApplication
import com.cube.cubeacademy.R
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utility.extention.isNetworkAvailable

class Repository(val api: ApiService) {
    // TODO: Add additional code if you need it

    private var applicationContext = MainApplication.application.baseContext

    /**
     * Used to check internet connection for call api.
     */
    private fun validateNetwork(): Boolean {
        return if (applicationContext.isNetworkAvailable()) {
            true
        } else {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.no_internet_connection_please_try_again_later), Toast.LENGTH_LONG).show()
            false
        }
    }

    suspend fun getAllNominations(): List<Nomination> {
        // TODO: Write the code to fetch the list nominations from the api
        return if (validateNetwork()) {
            api.getAllNominations().data
        } else {
            emptyList()
        }
    }

    suspend fun getAllNominees(): ArrayList<Nominee> {
        // TODO: Write the code to fetch list of all nominees from the api
        return if (validateNetwork()) {
            api.getAllNominees().data
        } else {
            arrayListOf()
        }
    }

    suspend fun createNomination(nomineeId: String, reason: String, process: String): Nomination? {
        // TODO: Write the code to create a new nomination using the api
        return if (validateNetwork()) {
            api.createNomination(nomineeId, reason, process).data
        } else {
            null
        }
    }
}