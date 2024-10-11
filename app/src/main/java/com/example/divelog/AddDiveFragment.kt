package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddDiveFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_dive, container, false)

        // Get references to the EditText fields and the button
        val diveLocation: EditText = view.findViewById(R.id.diveLocation)
        val maxDepth: EditText = view.findViewById(R.id.maxDepth)
        val duration: EditText = view.findViewById(R.id.duration)
        val saveDiveButton: Button = view.findViewById(R.id.saveDiveButton)

        // Set an OnClickListener on the save dive button
        saveDiveButton.setOnClickListener {
            // For debugging purposes, show a Toast
            val location = diveLocation.text.toString()
            val depth = maxDepth.text.toString()
            Toast.makeText(requireContext(), "Save Dive Button Clicked\nLocation: $location\nMax Depth: $depth", Toast.LENGTH_SHORT).show()

            // Here you would handle saving the dive details
            // For example, save to a database or local storage
        }

        return view
    }
}
