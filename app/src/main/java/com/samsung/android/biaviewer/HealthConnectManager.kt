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

import android.content.Context
import android.os.Build
import android.os.RemoteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.io.IOException
import java.time.Instant
import java.util.*

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    var availability = mutableStateOf(HealthConnectAvailability.NOT_SUPPORTED)
        private set

    val permissions = setOf(
        HealthPermission.createReadPermission(WeightRecord::class),
        HealthPermission.createReadPermission(HeightRecord::class),
        HealthPermission.createReadPermission(BodyFatRecord::class),
        HealthPermission.createReadPermission(BasalMetabolicRateRecord::class),
        HealthPermission.createReadPermission(NutritionRecord::class)
    )

    private var permissionsGranted = mutableStateOf(false)

    val requestPermissionActivityContract by lazy { PermissionController.createRequestPermissionResultContract() }

    private var uiState: UiState by mutableStateOf(UiState.Uninitialized)

    init {
        availability.value = when {
            HealthConnectClient.isAvailable(context) -> HealthConnectAvailability.INSTALLED
            isSupported() -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    suspend fun hasAllPermissions(): Boolean {
        return permissions == healthConnectClient.permissionController.getGrantedPermissions(
            permissions
        )
    }

    suspend fun readWeight(start: Instant, end: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        if (response.records.isNotEmpty()) {
            val weightRecord = response.records.last()
            return weightRecord.weight.inKilograms
        }
        return 0.0
    }

    suspend fun readHeight(start: Instant, end: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = HeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        if (response.records.isNotEmpty()) {
            val heightRecord = response.records.last()
            return heightRecord.height.inMeters
        }
        return 0.0
    }

    suspend fun readBodyFatRecord(start: Instant, end: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = BodyFatRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        if (response.records.isNotEmpty()) {
            val bodyFatRecord = response.records.last()
            return bodyFatRecord.percentage.value
        }
        return 0.0
    }

    suspend fun readBasalMetabolicRateRecord(start: Instant, end: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = BasalMetabolicRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        if (response.records.isNotEmpty()) {
            val basalMetabolicRateRecordRecord = response.records.last()
            return basalMetabolicRateRecordRecord.basalMetabolicRate.inKilocaloriesPerDay
        }
        return 0.0
    }

    suspend fun readNutritionRecord(start: Instant, end: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = NutritionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        if (response.records.isNotEmpty()) {
            val nutritionRecord = response.records.last()
            return nutritionRecord.energy!!.inKilocalories
        }
        return 0.0
    }

    suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = hasAllPermissions()
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

    private fun isSupported() = Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK
}

enum class HealthConnectAvailability {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}
