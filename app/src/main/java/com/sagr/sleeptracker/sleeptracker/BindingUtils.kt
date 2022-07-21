package com.sagr.sleeptracker.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sagr.sleeptracker.R
import com.sagr.sleeptracker.convertDurationToFormatted
import com.sagr.sleeptracker.convertNumericQualityToString
import com.sagr.sleeptracker.database.SleepNight


@BindingAdapter("textDurationFormatted")
fun TextView.setDurationFormat(night: SleepNight?) {
    night?.let {
        text =
            convertDurationToFormatted(night.startTimeMilli, night.endTimeMilli, context.resources)
    }
}

@BindingAdapter("qualityToString")
fun TextView.setSleepQualityString(night: SleepNight?) {
    night?.let {
        text = convertNumericQualityToString(night.sleepQuality, context.resources)
    }
}

@BindingAdapter("qualityImage")
fun ImageView.setQualityImage(night: SleepNight?) {
    night?.let {
        setImageResource(
            when (night.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            }
        )
    }
}