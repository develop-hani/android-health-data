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

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.lifecycle.lifecycleScope
import com.samsung.android.biaviewer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val TAG = "BIA Viewer"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var requestPermissions: ActivityResultLauncher<Set<HealthPermission>>
    private var mDecimalFormat: DecimalFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        healthConnectManager = HealthConnectManager(this)
        mDecimalFormat = DecimalFormat(getString(R.string.decimal_pattern))

        if (checkAvailability()) {
            createRequestPermissionsObject()
            checkPermissions(false)
            checkPermissionsAndRun(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.connect) {
            if (checkAvailability()) {
                checkPermissions(true)
            }
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun checkAvailability(): Boolean {
        val availability by healthConnectManager.availability

        when (availability) {
            HealthConnectAvailability.NOT_SUPPORTED -> {
                val notSupportedText = getString(
                    R.string.not_supported_description,
                    MIN_SUPPORTED_SDK
                )
                Toast.makeText(this, notSupportedText, Toast.LENGTH_LONG).show()
                return false
            }
            HealthConnectAvailability.NOT_INSTALLED -> {
                Toast.makeText(this, R.string.not_installed_description, Toast.LENGTH_LONG).show()
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun createRequestPermissionsObject() {
        requestPermissions =
            registerForActivityResult(healthConnectManager.requestPermissionActivityContract) { granted ->
                lifecycleScope.launch {
                    if (granted.isNotEmpty() && healthConnectManager.hasAllPermissions()) {
                        // Permissions successfully granted
                        Log.i(TAG, "Permissions successfully granted")
                        readAllData()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.permission_denied,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
    }

    private fun checkPermissions(showInfo: Boolean) {
        lifecycleScope.launch {
            try {
                if (healthConnectManager.hasAllPermissions()) {
                    if (showInfo) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.permission_granted,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                } else {
                    requestPermissions.launch(healthConnectManager.permissions)
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    Log.e(TAG, exception.toString())
                    Toast.makeText(this@MainActivity, "Error: $exception", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun checkPermissionsAndRun(showInfo: Boolean) {
        lifecycleScope.launch {
            try {
                if (healthConnectManager.hasAllPermissions()) {
                    readAllData()
                } else if (showInfo) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.permission_denied_short,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    Log.e(TAG, exception.toString())
                    Toast.makeText(this@MainActivity, "Error: $exception", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun onRefresh(@Suppress("UNUSED_PARAMETER") view: View) {
        if (checkAvailability()) {
            checkPermissionsAndRun(true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun readAllData() {
        lifecycleScope.launch {
            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val now = Instant.now()
            healthConnectManager.tryWithPermissionsCheck {
                val weight = healthConnectManager.readWeight(startOfDay.toInstant(), now)
                val height = healthConnectManager.readHeight(startOfDay.toInstant(), now)
                val bodyFat = healthConnectManager.readBodyFatRecord(startOfDay.toInstant(), now)
                val mbr =
                    healthConnectManager.readBasalMetabolicRateRecord(startOfDay.toInstant(), now)
                val calory = healthConnectManager.readNutritionRecord(startOfDay.toInstant(), now)

                runOnUiThread {
                    binding.textWeight.text =
                        mDecimalFormat!!.format(weight) + getString(R.string.kilograms)
                    var bmi = 0.0
                    if (height != 0.0) {
                        bmi = weight / (height * height)
                    }
                    binding.textBmi.text = mDecimalFormat!!.format(bmi)
                    binding.textBodyFat.text =
                        mDecimalFormat!!.format(bodyFat) + getString(R.string.percentage)
                    val fatMass = weight * bodyFat / 100.0
                    binding.textFatMass.text =
                        mDecimalFormat!!.format(fatMass) + getString(R.string.kilograms)
                    binding.textBmr.text =
                        mDecimalFormat!!.format(mbr) + getString(R.string.calories)
                    binding.textCalory.text =
                        mDecimalFormat!!.format(calory) + "KCal"
                }
            }
        }
    }
}