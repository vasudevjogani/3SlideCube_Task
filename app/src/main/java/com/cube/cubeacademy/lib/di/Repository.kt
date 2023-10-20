package com.cube.cubeacademy.lib.di

import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.models.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class Repository(val api: ApiService, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    // TODO: Add additional code if you need it

    /**
     *  Used to handle all errors using Result wrapper class for api call.
     */
    private suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        ResultWrapper.GenericError(throwable.code(), throwable.message())
                    }

                    else -> {
                        ResultWrapper.GenericError(null, throwable.message)
                    }
                }
            }
        }
    }


    suspend fun getAllNominations(): ResultWrapper<List<Nomination>> {
        // TODO: Write the code to fetch the list nominations from the api
        return safeApiCall(dispatcher = dispatcher) {
            api.getAllNominations().data
        }
    }

    suspend fun getAllNominees(): ResultWrapper<ArrayList<Nominee>> {
        // TODO: Write the code to fetch list of all nominees from the api
        return safeApiCall(dispatcher = dispatcher) {
            api.getAllNominees().data
        }
    }

    suspend fun createNomination(nomineeId: String, reason: String, process: String): ResultWrapper<Nomination?> {
        // TODO: Write the code to create a new nomination using the api
        return safeApiCall(dispatcher = dispatcher) {
            api.createNomination(nomineeId, reason, process).data
        }
    }
}