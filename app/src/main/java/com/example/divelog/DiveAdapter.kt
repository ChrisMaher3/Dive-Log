package com.example.divelog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiveAdapter(
    private val dives: List<Dive>,
    private val onLongClick: (Dive, Int) -> Unit
) : RecyclerView.Adapter<DiveAdapter.DiveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dive_item, parent, false)
        return DiveViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiveViewHolder, position: Int) {
        val dive = dives[position]
        holder.bind(dive)

        // Set the long click listener for deleting a dive
        holder.itemView.setOnLongClickListener {
            onLongClick(dive, position)
            true
        }
    }

    override fun getItemCount(): Int {
        return dives.size
    }

    class DiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val depthTextView: TextView = itemView.findViewById(R.id.depthTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(dive: Dive) {
            locationTextView.text = dive.location
            depthTextView.text = "${dive.maxDepth} m"
            durationTextView.text = "${dive.duration} min"
            dateTextView.text = dive.date
        }
    }
}
