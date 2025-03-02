package com.chris.divelog

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
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class SettingsFragment : Fragment() {

    private lateinit var lengthToggleGroup: RadioGroup
    private lateinit var tempToggleGroup: RadioGroup
    private lateinit var transferButton: Button
    private lateinit var importButton: Button
    private lateinit var faqButton: Button
    private lateinit var contactButton: Button
    private lateinit var versionText: TextView
    private lateinit var versionInfoCard: MaterialCardView

    companion object {
        private const val REQUEST_CODE_IMPORT_CSV = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lengthToggleGroup = view.findViewById(R.id.length_toggle_group)
        tempToggleGroup = view.findViewById(R.id.temp_toggle_group)
        transferButton = view.findViewById(R.id.transfer_button)
        importButton = view.findViewById(R.id.import_button)
        faqButton = view.findViewById(R.id.faq_button)
        contactButton = view.findViewById(R.id.contact_button)
        versionText = view.findViewById(R.id.version_text)
        versionInfoCard = view.findViewById(R.id.versionInfoCard)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true)
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true)

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

        lengthToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()
            when (checkedId) {
                R.id.rb_meters -> editor.putBoolean("isMeters", true)
                R.id.rb_feet -> editor.putBoolean("isMeters", false)
            }
            editor.apply()
        }

        tempToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()
            when (checkedId) {
                R.id.rb_celsius -> editor.putBoolean("isCelsius", true)
                R.id.rb_fahrenheit -> editor.putBoolean("isCelsius", false)
            }
            editor.apply()
        }

        transferButton.setOnClickListener {
            exportDiveLogToCSV()
        }

        importButton.setOnClickListener {
            importDiveLog()
        }

        faqButton.setOnClickListener {
            openFaqPage()
        }

        contactButton.setOnClickListener {
            openContactPage()
        }

        val appVersion = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        versionText.text = appVersion
    }

    private fun openFaqPage() {
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://docs.google.com/spreadsheets/d/11BjqJQgAKpSrwUx7YbRQSEVliDpE59HmtVVbc1fXxZ4/edit?usp=sharing"))
        startActivity(intent)
    }

    private fun openContactPage() {
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://example.com/contact"))
        startActivity(intent)
    }

    private fun exportDiveLogToCSV() {

        val context = requireContext()

        val dbHelper = DiveDatabaseHelper(context)

        val dives = dbHelper.getAllDives()



        if (dives.isEmpty()) {

            Toast.makeText(context, "No dive data to export", Toast.LENGTH_SHORT).show()

            return  // Corrected return statement

        }



        val csvFile = File(context.filesDir, "DiveLog.csv") // Moved outside the if-block

        try {

            FileWriter(csvFile).use { writer ->

                writer.append("ID,Location,Max Depth,Duration,Date,Buddy,Weather,Visibility,Water Temperature,Night Dive\n")

                for (dive in dives) {

                    writer.append("${dive.id},${dive.location},${dive.maxDepth},${dive.duration},${dive.date}," +

                            "${dive.diveBuddy},${dive.weatherConditions},${dive.visibility},${dive.waterTemperature},${dive.isNightDive}\n")

                }

            }

            shareCSVFile(csvFile)

        } catch (e: IOException) {

            Toast.makeText(context, "Failed to export data", Toast.LENGTH_SHORT).show()

        }

    }



    private fun shareCSVFile(csvFile: File) {

        val uri = FileProvider.getUriForFile(requireContext(), "com.chris.divelog.fileprovider", csvFile)

        val intent = Intent(Intent.ACTION_SEND).apply {

            type = "text/csv"

            putExtra(Intent.EXTRA_STREAM, uri)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        startActivity(Intent.createChooser(intent, "Share Dive Log CSV"))

    }

    private fun importDiveLog() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/csv"
        }
        startActivityForResult(intent, REQUEST_CODE_IMPORT_CSV)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMPORT_CSV && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                inputStream?.let {
                    importCsvDataToDatabase(it)
                }
            }
        }
    }

    private fun importCsvDataToDatabase(inputStream: InputStream) {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val dbHelper = DiveDatabaseHelper(requireContext())

        reader.use { bufferedReader ->
            var successCount = 0
            var errorCount = 0

            // Skip header
            bufferedReader.readLine()

            // Process each line
            bufferedReader.lineSequence()
                .forEach { line ->
                    try {
                        // Split the line into columns, handling quoted fields
                        val columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                            .map { it.trim().removeSurrounding("\"") }

                        if (columns.size != 10) {
                            errorCount++
                            return@forEach
                        }

                        // Create Dive object from CSV columns
                        val dive = Dive(
                            id = 0,  // Auto-generated by database
                            location = columns[1],
                            maxDepth = columns[2].toFloat(),
                            duration = columns[3].toInt(),
                            date = columns[4],
                            diveBuddy = columns[5],
                            weatherConditions = columns[6],
                            visibility = columns[7].toFloat(),
                            waterTemperature = columns[8].toFloat(),
                            isNightDive = columns[9].toBoolean()
                        )

                        // Add dive to database
                        dbHelper.addDive(dive)
                        successCount++
                    } catch (e: Exception) {
                        errorCount++
                    }
                }

            // Show import results
            val message = buildString {
                append("Import complete: $successCount dives imported")
                if (errorCount > 0) append("\n$errorCount entries could not be imported")
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}