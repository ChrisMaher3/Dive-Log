package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DiveDetailFragment : Fragment() {

    private lateinit var dive: Dive

    companion object {
        private const val ARG_DIVE = "dive"

        fun newInstance(dive: Dive): DiveDetailFragment {
            val fragment = DiveDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_DIVE, dive)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dive = it.getParcelable(ARG_DIVE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dive_detail, container, false)

        // Set up views
        val locationTextView: TextView = view.findViewById(R.id.diveLocationTextView)
        val depthTextView: TextView = view.findViewById(R.id.maxDepthTextView)
        val durationTextView: TextView = view.findViewById(R.id.diveDurationTextView)
        val dateTextView: TextView = view.findViewById(R.id.diveDateTextView)
        val buddyTextView: TextView = view.findViewById(R.id.diveBuddyTextView)
        val weatherTextView: TextView = view.findViewById(R.id.weatherConditionsTextView)
        val visibilityTextView: TextView = view.findViewById(R.id.visibilityTextView)
        val nightDiveTextView: TextView = view.findViewById(R.id.nightDiveTextView)

        // Populate the views with dive data
        locationTextView.text = dive.location
        depthTextView.text = "Max Depth: ${dive.maxDepth} m"
        durationTextView.text = "Duration: ${dive.duration} min"
        dateTextView.text = "Date: ${dive.date}"
        buddyTextView.text = "Dive Buddy: ${dive.diveBuddy ?: "N/A"}"
        weatherTextView.text = "Weather Conditions: ${dive.weatherConditions ?: "N/A"}"
        visibilityTextView.text = "Visibility: ${dive.visibility?.toString() ?: "N/A"} m"
        nightDiveTextView.text = if (dive.isNightDive) "Night Dive: Yes" else "Night Dive: No"

        return view
    }
}
