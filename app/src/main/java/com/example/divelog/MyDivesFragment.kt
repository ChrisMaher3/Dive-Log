package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class MyDivesFragment : Fragment() {

    private var dives: List<Dive> = listOf() // List to hold Dive objects

    companion object {
        fun newInstance(dives: List<Dive>): MyDivesFragment {
            val fragment = MyDivesFragment()
            val args = Bundle()
            args.putParcelableArrayList("dives", ArrayList(dives)) // Store dives as a ParcelableArrayList
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the dives from the arguments
        arguments?.let {
            dives = it.getParcelableArrayList("dives") ?: listOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_dives, container, false)

        // Find the TextView to display dive information
        val divesTextView: TextView = view.findViewById(R.id.divesTextView)

        // Format and display the dive details in the TextView, including date
        divesTextView.text = dives.joinToString("\n") {
            "${it.location} - ${it.maxDepth}m - ${it.duration}min - Date: ${it.date}"
        }

        return view
    }
}
