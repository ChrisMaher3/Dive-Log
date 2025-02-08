package com.chris.divelog

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
import android.util.Log

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
        val view = inflater.inflate(R.layout.fragment_add_certification, container, false)

        organizationEditText = view.findViewById(R.id.organizationEditText)
        yearEditText = view.findViewById(R.id.yearEditText)
        certificationNameEditText = view.findViewById(R.id.certificationNameEditText)
        uploadImageButton = view.findViewById(R.id.uploadImageButton)
        addCertificationButton = view.findViewById(R.id.addCertificationButton)
        certificationImageView = view.findViewById(R.id.certificationImageView)

        uploadImageButton.setOnClickListener {
            openImageChooser()
        }

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
            certificationImageView.setImageURI(selectedImageUri)
        }
    }

    private fun addCertification() {
        val organization = organizationEditText.text.toString()
        val year = yearEditText.text.toString()
        val certificationName = certificationNameEditText.text.toString()

        if (organization.isBlank() || year.isBlank() || certificationName.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val yearInt = year.toInt()
            if (yearInt < 1900 || yearInt > 2100) {
                Toast.makeText(requireContext(), "Please enter a valid year", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Please enter a numeric year", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUriString = selectedImageUri?.toString() ?: ""

        val newCertification = Certification(
            name = certificationName,
            organization = organization,
            year = year,
            imageUri = imageUriString
        )

        val dbHelper = DiveDatabaseHelper(requireContext())

        try {
            dbHelper.addCertification(newCertification) // Assuming this doesn't return a value

            Toast.makeText(requireContext(), "Certification added!", Toast.LENGTH_SHORT).show()

            val previousFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container) as? ViewCertificationsFragment
            previousFragment?.loadCertifications()

            requireActivity().supportFragmentManager.popBackStack()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to add certification", Toast.LENGTH_SHORT).show()
        }
    }

}
