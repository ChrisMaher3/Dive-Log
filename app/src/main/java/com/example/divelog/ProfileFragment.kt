package com.example.divelog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var totalDivesTextView: TextView
    private lateinit var uniqueLocationsTextView: TextView
    private lateinit var maxDepthTextView: TextView
    private lateinit var totalTimeDivingTextView: TextView
    private lateinit var averageDepthTextView: TextView
    private lateinit var totalDiveDaysTextView: TextView
    private lateinit var viewCertificationsButton: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var diveRepository: DiveRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        totalDivesTextView = view.findViewById(R.id.totalDivesTextView)
        uniqueLocationsTextView = view.findViewById(R.id.uniqueLocationsTextView)
        maxDepthTextView = view.findViewById(R.id.maxDepthTextView)
        totalTimeDivingTextView = view.findViewById(R.id.totalTimeDivingTextView)
        averageDepthTextView = view.findViewById(R.id.averageDepthTextView)
        totalDiveDaysTextView = view.findViewById(R.id.totalDiveDaysTextView)
        viewCertificationsButton = view.findViewById(R.id.viewCertificationsButton)

        // Initialize dive repository
        diveRepository = DiveRepository(requireContext())

        // Load profile data
        loadProfileImage()
        loadSavedProfile()
        loadDiveStats()

        // Profile image click listener
        profileImageView.setOnClickListener {
            openImageChooser()
        }

        // Navigate to certifications
        viewCertificationsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewCertificationsFragment()) // Ensure this ID matches your container in activity
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    // Load profile image from URI using Glide
    private fun loadProfileImage() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedImageUri = sharedPref.getString("profile_image_uri", null)

        if (savedImageUri != null) {
            loadProfileImageFromUri(Uri.parse(savedImageUri))
        } else {
            Glide.with(this)
                .load(R.drawable.face) // Default image
                .circleCrop()
                .into(profileImageView)
        }
    }

    // Load profile image from URI using Glide
    private fun loadProfileImageFromUri(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .circleCrop()
            .placeholder(R.drawable.face) // Optional: Placeholder while loading
            .error(R.drawable.face) // Optional: Error image if loading fails
            .into(profileImageView)
    }

    // Opens the image chooser for profile image
    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // Handling the image picker result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri) // Set the selected image to the ImageView
            saveProfileImage(selectedImageUri) // Save the image URI
            loadProfileImageFromUri(selectedImageUri) // Reload using Glide
        }
    }

    // Save profile image URI to shared preferences
    private fun saveProfileImage(imageUri: Uri?) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("profile_image_uri", imageUri.toString())
            apply()
        }
    }

    // Load profile name from shared preferences
    private fun loadSavedProfile() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("profile_name", "")
        nameEditText.setText(savedName)
    }

    // Save profile name when user exits the screen
    override fun onPause() {
        super.onPause()
        saveProfileName()
    }

    private fun saveProfileName() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("profile_name", nameEditText.text.toString())
            apply()
        }
    }

    // Load dive statistics from the repository
    private fun loadDiveStats() {
        val dives = diveRepository.getAllDives()

        // Calculate total dives
        val totalDives = dives.size

        // Calculate total unique locations (case-insensitive)
        val uniqueLocations = dives.map { it.location.lowercase(Locale.getDefault()) }.distinct().size

        // Calculate max depth
        val maxDepth = dives.maxOfOrNull { it.maxDepth } ?: 0f

        // Calculate total time diving (in minutes)
        val totalTimeInMinutes = dives.sumOf { it.duration }
        val totalHours = totalTimeInMinutes / 60
        val totalMinutes = totalTimeInMinutes % 60

        // Calculate average depth
        val averageDepth = if (totalDives > 0) {
            dives.map { it.maxDepth }.average().toFloat()
        } else {
            0f
        }

        // Calculate total dive days (assuming each dive represents a unique day)
        val totalDiveDays = dives.map { it.date }.distinct().count()

        // Update the TextViews with the stats
        totalDivesTextView.text = "Total Dives: $totalDives"
        uniqueLocationsTextView.text = "Unique Locations: $uniqueLocations"
        maxDepthTextView.text = "Max Depth: ${String.format("%.1f", maxDepth)}m"
        totalTimeDivingTextView.text = "Total Dive Time: ${totalHours}h ${totalMinutes}min"
        averageDepthTextView.text = "Average Depth: ${String.format("%.1f", averageDepth)}m"
        totalDiveDaysTextView.text = "Total Dive Days: $totalDiveDays"
    }
}
