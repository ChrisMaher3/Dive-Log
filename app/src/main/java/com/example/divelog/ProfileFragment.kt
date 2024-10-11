package com.example.divelog

import android.app.Activity
import android.content.Intent
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)

        loadProfileImage()

        profileImageView.setOnClickListener {
            openImageChooser()
        }

        return view
    }

    private fun loadProfileImage() {
        val imageUrl = "https://example.com/path/to/profile/image.jpg" // Default or previously uploaded image URL

        Glide.with(this)
            .load(imageUrl)
            .circleCrop() // Crop the image into a circle
            .placeholder(R.drawable.face) // Optional: Placeholder while loading
            .error(R.drawable.face) // Optional: Error image if loading fails
            .into(profileImageView)
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
            loadProfileImageFromUri(selectedImageUri) // Load the image using Glide
        }
    }

    private fun loadProfileImageFromUri(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .circleCrop() // Ensure it's cropped into a circle
            .placeholder(R.drawable.face) // Optional: Placeholder while loading
            .error(R.drawable.face) // Optional: Error image if loading fails
            .into(profileImageView)
    }

    private fun uploadImageToServer(imageUri: Uri?) {
        // Implement the logic to upload the image to your server or storage
    }
}
