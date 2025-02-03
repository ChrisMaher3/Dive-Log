package com.example.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager

class DiveAdapter(private val context: Context, private val dives: List<Dive>) : BaseAdapter() {

    override fun getCount(): Int {
        return dives.size
    }

    override fun getItem(position: Int): Dive {
        return dives[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.dive_list_item, parent, false)

        // Get the Dive object for the current position
        val dive = getItem(position)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isMetersSelected = sharedPreferences.getBoolean("isMeters", true) // Default to meters

        // Find and populate the views with dive data
        val locationTextView: TextView = view.findViewById(R.id.locationTextView)
        val depthTextView: TextView = view.findViewById(R.id.depthTextView)
        val durationTextView: TextView = view.findViewById(R.id.durationTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)

        // Format depth with 1 decimal place
        locationTextView.text = dive.location
        depthTextView.text = "Depth: ${"%.1f".format(dive.maxDepth)} ${if (isMetersSelected) "m" else "ft"}"
        durationTextView.text = "Duration: ${dive.duration} min"
        dateTextView.text = "Date: ${dive.date}"

        return view
    }
}
