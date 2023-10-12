/*
 * Copyright 2022 Samsung Electronics Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsung.android.biaviewer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class HealthConnectManagerInstrumentedTest {
    @Test
    fun shouldPackageBeSetToBiaViewer() {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        //then
        assertEquals("com.samsung.android.biaviewer", appContext.packageName)
    }

    @Test
    fun shouldAvailabilityBeSetToInstalled() {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        //then
        assertEquals(HealthConnectAvailability.INSTALLED, healthConnectManager.availability.value)
    }

    @Test
    fun shouldHasAllPermissions(): Unit = runBlocking {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        launch(Dispatchers.Default) {
            //when
            val permissions = healthConnectManager.hasAllPermissions()

            //then
            assertTrue(permissions)
        }

    }

    @Test
    fun shouldNotThrowExceptionWhenReadWeightIsCalled(): Unit = runBlocking {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        launch(Dispatchers.Default) {
            //when
            try {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                healthConnectManager.readWeight(startOfDay.toInstant(), Instant.now())
            } catch (e: Exception) {
                assertFalse(true)
            }

            //then
            assertTrue(true)
        }

    }

    @Test
    fun shouldNotThrowExceptionWhenReadHeightIsCalled(): Unit = runBlocking {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        launch(Dispatchers.Default) {
            //when
            try {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                healthConnectManager.readHeight(startOfDay.toInstant(), Instant.now())
            } catch (e: Exception) {
                assertFalse(true)
            }

            //then
            assertTrue(true)
        }
    }

    @Test
    fun shouldNotThrowExceptionWhenReadBodyFatRecordIsCalled(): Unit = runBlocking {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        launch(Dispatchers.Default) {
            //when
            try {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                healthConnectManager.readBodyFatRecord(startOfDay.toInstant(), Instant.now())
            } catch (e: Exception) {
                assertFalse(true)
            }

            //then
            assertTrue(true)
        }
    }

    @Test
    fun shouldNotThrowExceptionWhenReadBasalMetabolicRateRecordIsCalled(): Unit = runBlocking {
        //given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val healthConnectManager = HealthConnectManager(appContext)

        launch(Dispatchers.Default) {
            //when
            try {
                val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                healthConnectManager.readBasalMetabolicRateRecord(
                    startOfDay.toInstant(),
                    Instant.now()
                )
            } catch (e: Exception) {
                assertFalse(true)
            }

            //then
            assertTrue(true)
        }
    }
}