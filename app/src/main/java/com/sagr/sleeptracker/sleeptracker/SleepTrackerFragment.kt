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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sagr.sleeptracker.R
import com.sagr.sleeptracker.database.SleepDatabase
import com.sagr.sleeptracker.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )


        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[SleepTrackerViewModel::class.java]

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.lifecycleOwner = this

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner) { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(
                        night.nightId
                    )
                )
                sleepTrackerViewModel.onDoneNavigation()
            }
        }
        sleepTrackerViewModel.navigateToSleepDetail.observe(viewLifecycleOwner) { night ->
            night?.let {

                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(night)
                )
                sleepTrackerViewModel.onNavigateToSleepDetail()
            }
        }
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager
        val adapter = SleepTrackerAdapter(SleepNightListener { nightId ->
            sleepTrackerViewModel.onSleepNightClicked(nightId)

        })
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.allNights.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        }

        return binding.root
    }
}
