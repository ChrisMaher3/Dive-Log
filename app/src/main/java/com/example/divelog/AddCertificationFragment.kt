package com.example.divelog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddCertificationFragment : Fragment() {

    private lateinit var organizationEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var certificationNameEditText: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var addCertificationButton: Button
    private lateinit var certificationImageView: ImageView

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_certification, container, false)

        // Initialize views
        organizationEditText = view.findViewById(R.id.organizationEditText)
        yearEditText = view.findViewById(R.id.yearEditText)
        certificationNameEditText = view.findViewById(R.id.certificationNameEditText)
        uploadImageButton = view.findViewById(R.id.uploadImageButton)
        addCertificationButton = view.findViewById(R.id.addCertificationButton)
        certificationImageView = view.findViewById(R.id.certificationImageView)

        // Set click listener for the upload image button
        uploadImageButton.setOnClickListener {
            openImageChooser()
        }

        // Set click listener for the add certification button
        addCertificationButton.setOnClickListener {
            addCertification()
        }

        return view
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Certification Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            certificationImageView.setImageURI(selectedImageUri) // Display the selected image
        }
    }

    private fun addCertification() {
        val organization = organizationEditText.text.toString()
        val year = yearEditText.text.toString()
        val certificationName = certificationNameEditText.text.toString()

        // Validate input fields
        if (organization.isBlank() || year.isBlank() || certificationName.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the new Certification object with the image URI
        val newCertification = Certification(certificationName, organization, year, selectedImageUri.toString())

        // Here you should save newCertification to your repository or pass it back to the previous fragment
        // Example: certificationRepository.addCertification(newCertification)

        // Notify the user and navigate back
        Toast.makeText(requireContext(), "Certification added!", Toast.LENGTH_SHORT).show()

        // Navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }
}
