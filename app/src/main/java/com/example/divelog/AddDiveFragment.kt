package com.example.divelog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class AddDiveFragment : Fragment() {

    private lateinit var diveDateTextView: TextView
    private lateinit var diveLocation: EditText
    private lateinit var maxDepth: EditText
    private lateinit var duration: EditText
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_dive, container, false)

        // Get references to the EditText fields and the button
        diveLocation = view.findViewById(R.id.diveLocation)
        maxDepth = view.findViewById(R.id.maxDepth)
        duration = view.findViewById(R.id.diveDuration) // Duration field
        diveDateTextView = view.findViewById(R.id.diveDateTextView) // TextView to display selected date
        val saveDiveButton: Button = view.findViewById(R.id.saveDiveButton)

        // Set an OnClickListener on the dive date TextView to show DatePickerDialog
        diveDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        // Set an OnClickListener on the save dive button
        saveDiveButton.setOnClickListener {
            val location = diveLocation.text.toString()
            val depth = maxDepth.text.toString().toFloatOrNull()
            val diveDuration = duration.text.toString().toIntOrNull() // Get duration input

            if (location.isNotEmpty() && depth != null && diveDuration != null && selectedDate.isNotEmpty()) {
                val newDive = Dive(location, depth, diveDuration, selectedDate) // Pass the selected date
                (activity as MainActivity).addDive(newDive) // Add the new dive to MainActivity's list

                Toast.makeText(requireContext(), "Dive Saved: $location, Depth: $depth m, Duration: $diveDuration min on $selectedDate", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = dateFormat.format(date.time) // Save the selected date in the desired format
            diveDateTextView.text = selectedDate // Update the TextView to show the selected date
        }, year, month, day)

        datePickerDialog.show()
    }
}
