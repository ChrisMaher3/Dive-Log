package com.chris.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

class DiveAdapter(
    private val context: Context,
    private val dives: List<Dive>,
    private val onClick: (Dive) -> Unit,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<DiveAdapter.DiveViewHolder>() {

    inner class DiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val depthTextView: TextView = itemView.findViewById(R.id.depthTextView)
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        init {
            itemView.setOnClickListener {
                onClick(dives[adapterPosition])
            }
            itemView.setOnLongClickListener {
                onLongClick(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dive_list_item, parent, false)
        return DiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiveViewHolder, position: Int) {
        val dive = dives[position]
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isMetersSelected = sharedPreferences.getBoolean("isMeters", true)

        holder.locationTextView.text = dive.location
        holder.depthTextView.text = "Depth: ${"%.1f".format(dive.maxDepth)} ${if (isMetersSelected) "m" else "ft"}"
        holder.durationTextView.text = "Duration: ${dive.duration} min"
        holder.dateTextView.text = "Date: ${dive.date}"
    }

    override fun getItemCount(): Int = dives.size
}
