package com.chris.divelog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.chris.divelog.databinding.FragmentAddDiveBinding
import java.text.SimpleDateFormat
import java.util.*

class AddDiveFragment : Fragment() {
    private var _binding: FragmentAddDiveBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: String = ""
    private var isNightDive: Boolean = false
    private var unitDepth: String = "m"
    private var unitTemp: String = "°C"
    private var unitVisibility: String = "m"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the user's unit preferences from SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true)
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true)

        // Set units based on the preferences
        unitDepth = if (isMeters) "m" else "ft"
        unitTemp = if (isCelsius) "°C" else "°F"
        unitVisibility = if (isMeters) "m" else "ft"

        // Display the units next to the fields
        binding.maxDepth.setHint("Max Depth ($unitDepth)")
        binding.waterTemperature.setHint("Water Temperature ($unitTemp)")
        binding.visibility.setHint("Visibility ($unitVisibility)")

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

        // Get SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true) // true = meters, false = feet
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true) // true = Celsius, false = Fahrenheit

        // If the depth is in feet, convert it to meters
        val finalDepth = if (depth != null) {
            if (!isMeters) {
                (depth * 0.3048).toFloat() // Convert feet to meters and cast to Float
            } else {
                depth // Keep it in meters if the user has selected meters
            }
        } else {
            null
        }

        // If the water temperature is in Fahrenheit, convert it to Celsius
        val finalTemperature = if (waterTemperature != null) {
            if (!isCelsius) {
                ((waterTemperature - 32) * 5 / 9).toFloat() // Convert Fahrenheit to Celsius and cast to Float
            } else {
                waterTemperature // Keep it in Celsius if the user has selected Celsius
            }
        } else {
            null
        }

        // If required fields are valid, save the dive
        if (location.isNotEmpty() && finalDepth != null && diveDuration != null && selectedDate.isNotEmpty()) {
            val newDive = Dive(
                location, finalDepth, diveDuration, selectedDate,
                diveBuddy, weatherConditions, visibility, finalTemperature, isNightDive
            )
            (activity as MainActivity).addDive(newDive)

            Toast.makeText(
                requireContext(),
                "Dive Saved: $location, Depth: ${finalDepth}m, Duration: $diveDuration min, Water Temp: $finalTemperature°C on $selectedDate",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields correctly", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
