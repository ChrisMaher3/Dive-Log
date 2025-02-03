package com.example.divelog

import android.app.DatePickerDialog // Make sure this import is present
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.divelog.databinding.FragmentAddDiveBinding
import java.text.SimpleDateFormat
import java.util.*

class AddDiveFragment : Fragment() {
    private var _binding: FragmentAddDiveBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: String = ""
    private var isNightDive: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set onClickListener for dive date selection
        binding.diveDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        // Set onCheckedChangeListener for Night Dive switch
        binding.nightDiveSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            isNightDive = isChecked
            if (isChecked) {
                Toast.makeText(requireContext(), "Night dive selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Dive button listener
        binding.saveDiveButton.setOnClickListener {
            saveDive()
        }
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
            selectedDate = dateFormat.format(date.time)
            binding.diveDateTextView.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun saveDive() {
        val location = binding.diveLocation.text.toString()
        val depth = binding.maxDepth.text.toString().toFloatOrNull()
        val diveDuration = binding.diveDuration.text.toString().toIntOrNull()
        val diveBuddy = binding.diveBuddy.text.toString()
        val weatherConditions = binding.weatherConditions.text.toString()
        val visibility = binding.visibility.text.toString().toFloatOrNull()

        // Get water temperature value
        val waterTemperature = binding.waterTemperature.text.toString().toFloatOrNull()

        if (location.isNotEmpty() && depth != null && diveDuration != null && selectedDate.isNotEmpty()) {
            val newDive = Dive(location, depth, diveDuration, selectedDate, diveBuddy, weatherConditions, visibility, waterTemperature, isNightDive)
            (activity as MainActivity).addDive(newDive)

            Toast.makeText(requireContext(), "Dive Saved: $location, Depth: $depth m, Duration: $diveDuration min, Water Temp: $waterTemperature°C on $selectedDate", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}