package com.cube.cubeacademy

import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.ResultWrapper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: Repository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun getNominationsTest() = runBlocking {
        // TODO: Write a test for getting all the nominations from the mock api
        when (val results = repository.getAllNominations()) {
            is ResultWrapper.GenericError -> TODO()
            is ResultWrapper.NetworkError -> TODO()
            is ResultWrapper.Success -> {
                Assert.assertEquals(4, results.value.size)
            }
        }
    }

    @Test
    fun getNomineesTest() = runBlocking {
        // TODO: Write a test for getting all the nominees from the mock api
        when (val results = repository.getAllNominees()) {
            is ResultWrapper.GenericError -> TODO()
            is ResultWrapper.NetworkError -> TODO()
            is ResultWrapper.Success -> {
                Assert.assertEquals(3, results.value.size)
            }
        }
    }

    @Test
    fun createNominationTest() = runBlocking {
        // TODO: Write a test for creating a new nomination using the mock api
        when (val results = repository.createNomination("123", "good", "unfair")) {
            is ResultWrapper.GenericError -> TODO()
            is ResultWrapper.NetworkError -> TODO()
            is ResultWrapper.Success -> {
                Assert.assertEquals("3", results.value?.nominationId)
            }
        }
    }
}