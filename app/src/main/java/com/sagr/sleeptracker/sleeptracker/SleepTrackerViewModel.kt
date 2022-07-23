/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sagr.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sagr.sleeptracker.database.SleepDatabaseDao
import com.sagr.sleeptracker.database.SleepNight
import com.sagr.sleeptracker.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var _navigateToSleepQuality = MutableLiveData<SleepNight>()


    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality


    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var tonight = MutableLiveData<SleepNight?>()

    var allNights = database.getAllNights()

    /**
     * Converted nights to Spanned for displaying.
     */
    val nightsString = Transformations.map(allNights) { nights ->
        formatNights(nights, application.resources)
    }

    init {
        initTonight()
    }


    private fun initTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }


    fun onStartTracking() {
        uiScope.launch {
            val night = SleepNight()
            insert(night)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)

        }
    }


    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearAll()
        }
    }


    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night: SleepNight?
        withContext(Dispatchers.IO) {
            night = database.getTonight()

        }
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }
        return night

    }


    fun onDoneNavigation() {
        _navigateToSleepQuality.value = null
    }


    var startButtonEnabled = Transformations.map(tonight) {
        it == null
    }

    var stopButtonEnabled = Transformations.map(tonight) {
        null != it
    }

    var clearButtonEnabled = Transformations.map(allNights) {
        it.isNotEmpty()
    }

    private val _navigateToSleepDetail = MutableLiveData<Long>()
    val navigateToSleepDetail
        get() = _navigateToSleepDetail

    // Handling navigating to [SleepDetailFragment]
    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDetail.value = id
    }

    fun onNavigateToSleepDetail() {
        _navigateToSleepDetail.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

