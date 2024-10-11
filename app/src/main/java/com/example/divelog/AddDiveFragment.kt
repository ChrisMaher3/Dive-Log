package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddDiveFragment : Fragment() { // Fixed class name to start with a capital letter

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
        val duration: EditText = view.findViewById(R.id.diveDuration) // Duration field
        val saveDiveButton: Button = view.findViewById(R.id.saveDiveButton)

        // Set an OnClickListener on the save dive button
        saveDiveButton.setOnClickListener {
            val location = diveLocation.text.toString()
            val depth = maxDepth.text.toString().toFloatOrNull()
            val diveDuration = duration.text.toString().toIntOrNull() // Get duration input

            // Validate the input fields
            if (location.isNotEmpty() && depth != null && diveDuration != null) {
                val newDive = Dive(location, depth, diveDuration)
                (activity as MainActivity).addDive(newDive) // Save the new dive to the database

                Toast.makeText(requireContext(), "Dive Saved: $location, Depth: $depth m, Duration: $diveDuration min", Toast.LENGTH_SHORT).show()

                // Optionally, clear the input fields after saving
                diveLocation.text.clear()
                maxDepth.text.clear()
                duration.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
