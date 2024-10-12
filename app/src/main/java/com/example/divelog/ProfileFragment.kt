package com.example.divelog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        // Initialize views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)

        // Load saved profile data (name and image)
        loadProfileData()

        // Set click listener to upload a new profile picture
        profileImageView.setOnClickListener {
            openImageChooser()
        }

        return view
    }

    private fun loadProfileData() {
        // Load name from SharedPreferences
        val savedName = sharedPreferences.getString("user_name", "")
        nameEditText.setText(savedName)

        // Load profile picture URI from SharedPreferences
        val savedImageUriString = sharedPreferences.getString("profile_image_uri", null)
        val savedImageUri = savedImageUriString?.let { Uri.parse(it) }

        // Load image using Glide, or a placeholder if not available
        loadProfileImageFromUri(savedImageUri)
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            loadProfileImageFromUri(selectedImageUri)

            // Save the selected image URI to SharedPreferences
            saveProfileImageUri(selectedImageUri)
        }
    }

    private fun loadProfileImageFromUri(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .circleCrop()
            .placeholder(R.drawable.face) // Placeholder image
            .error(R.drawable.face) // Error image
            .into(profileImageView)
    }

    private fun saveProfileImageUri(uri: Uri?) {
        // Save the URI as a string in SharedPreferences
        with(sharedPreferences.edit()) {
            putString("profile_image_uri", uri?.toString())
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        // Save the user's name when the fragment is paused
        saveUserName()
    }

    private fun saveUserName() {
        // Save the user's name in SharedPreferences
        val userName = nameEditText.text.toString()
        with(sharedPreferences.edit()) {
            putString("user_name", userName)
            apply()
        }
    }
}
