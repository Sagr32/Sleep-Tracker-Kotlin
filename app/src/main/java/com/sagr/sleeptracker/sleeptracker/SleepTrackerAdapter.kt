package com.sagr.sleeptracker.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sagr.sleeptracker.R
import com.sagr.sleeptracker.convertDurationToFormatted
import com.sagr.sleeptracker.convertNumericQualityToString
import com.sagr.sleeptracker.database.SleepNight


class SleepTrackerAdapter : RecyclerView.Adapter<SleepTrackerAdapter.ViewHolder>() {

    var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context.resources

        holder.sleepLength.text = convertDurationToFormatted(item.startTimeMilli,item.endTimeMilli,res)
        holder.sleepQuality.text = convertNumericQualityToString(item.sleepQuality,res)
        holder.qualityImage.setImageResource(when (item.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_sleep_night, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var sleepLength: TextView = itemView.findViewById(R.id.sleep_length)

        var sleepQuality: TextView = itemView.findViewById(R.id.quality_string)

        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

    }
}