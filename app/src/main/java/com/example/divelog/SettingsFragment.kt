package com.example.divelog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.card.MaterialCardView

class SettingsFragment : Fragment() {

    private lateinit var lengthToggleGroup: RadioGroup
    private lateinit var tempToggleGroup: RadioGroup
    private lateinit var transferButton: Button
    private lateinit var faqButton: Button
    private lateinit var contactButton: Button
    private lateinit var versionText: TextView
    private lateinit var versionInfoCard: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the UI elements
        lengthToggleGroup = view.findViewById(R.id.length_toggle_group)
        tempToggleGroup = view.findViewById(R.id.temp_toggle_group)
        transferButton = view.findViewById(R.id.transfer_button)
        faqButton = view.findViewById(R.id.faq_button)
        contactButton = view.findViewById(R.id.contact_button)
        versionText = view.findViewById(R.id.version_text)
        versionInfoCard = view.findViewById(R.id.versionInfoCard)

        // Load saved preferences for unit settings
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true)
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true)

        // Set the radio buttons based on saved preferences
        if (isMeters) {
            view.findViewById<RadioButton>(R.id.rb_meters).isChecked = true
        } else {
            view.findViewById<RadioButton>(R.id.rb_feet).isChecked = true
        }

        if (isCelsius) {
            view.findViewById<RadioButton>(R.id.rb_celsius).isChecked = true
        } else {
            view.findViewById<RadioButton>(R.id.rb_fahrenheit).isChecked = true
        }

        // Handle the radio buttons for Meters/Feet
        lengthToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()
            when (checkedId) {
                R.id.rb_meters -> editor.putBoolean("isMeters", true)
                R.id.rb_feet -> editor.putBoolean("isMeters", false)
            }
            editor.apply()
        }

        // Handle the radio buttons for Celsius/Fahrenheit
        tempToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()
            when (checkedId) {
                R.id.rb_celsius -> editor.putBoolean("isCelsius", true)
                R.id.rb_fahrenheit -> editor.putBoolean("isCelsius", false)
            }
            editor.apply()
        }

        // Handle Transfer Data button click
        transferButton.setOnClickListener {
            // Implement the data transfer functionality (e.g., sync, export, etc.)
        }

        // Handle FAQ button click
        faqButton.setOnClickListener {
            openFaqPage()
        }

        // Handle Contact button click
        contactButton.setOnClickListener {
            openContactPage()
        }

        // Display app version in the Version Card
        val appVersion = "Version 1.0.0" // This can be dynamically fetched
        versionText.text = appVersion
    }

    private fun openFaqPage() {
        // You can navigate to an FAQ page or show a dialog
        // Example: open a web page for FAQ
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://example.com/faq"))
        startActivity(intent)
    }

    private fun openContactPage() {
        // You can open the contact page or show a contact form
        // Example: open a web page for contact
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://example.com/contact"))
        startActivity(intent)
    }
}
