package com.example.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

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

        // Find and populate the views with dive data
        val locationTextView: TextView = view.findViewById(R.id.locationTextView)
        val depthTextView: TextView = view.findViewById(R.id.depthTextView)
        val durationTextView: TextView = view.findViewById(R.id.durationTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)

        // Set dive data into the views
        locationTextView.text = dive.location
        depthTextView.text = "Depth: ${dive.maxDepth} m"
        durationTextView.text = "Duration: ${dive.duration} min"
        dateTextView.text = "Date: ${dive.date}"

        return view
    }
}
