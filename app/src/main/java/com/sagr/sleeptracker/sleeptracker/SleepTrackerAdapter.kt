package com.sagr.sleeptracker.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sagr.sleeptracker.database.SleepNight
import com.sagr.sleeptracker.databinding.ListItemSleepNightBinding


class SleepTrackerAdapter(private val clickListener: SleepNightListener) : ListAdapter<SleepNight,
        SleepTrackerAdapter.ViewHolder>(SleepNightDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(clickListener, item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder(private val binding: ListItemSleepNightBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(
            clickListener: SleepNightListener, item: SleepNight
        ) {
            binding.sleepNight = item
            binding.sleepListener = clickListener
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ListItemSleepNightBinding.inflate(layoutInflater)

                return ViewHolder(binding)
            }
        }
    }


}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}


class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}