package com.cube.cubeacademy

import com.cube.cubeacademy.lib.di.Repository
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
        val results = repository.getAllNominations()
        Assert.assertEquals(4, results.size)
    }

    @Test
    fun getNomineesTest() = runBlocking {
        // TODO: Write a test for getting all the nominees from the mock api
        val result = repository.getAllNominees()
        Assert.assertEquals(3, result.size)
    }

    @Test
    fun createNominationTest() = runBlocking {
        // TODO: Write a test for creating a new nomination using the mock api
        val result = repository.createNomination("123", "good", "unfair")
        Assert.assertEquals("3", result?.nominationId)
    }
}