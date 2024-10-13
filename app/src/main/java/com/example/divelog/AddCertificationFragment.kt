package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddCertificationFragment : Fragment() {

    private lateinit var certificationNameEditText: EditText
    private lateinit var certificationDateEditText: EditText
    private lateinit var certificationAgencyEditText: EditText
    private lateinit var saveCertificationButton: Button

    private lateinit var certificationRepository: CertificationRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_certification, container, false)

        // Initialize views
        certificationNameEditText = view.findViewById(R.id.certificationNameEditText)
        certificationDateEditText = view.findViewById(R.id.certificationDateEditText)
        certificationAgencyEditText = view.findViewById(R.id.certificationAgencyEditText)
        saveCertificationButton = view.findViewById(R.id.saveCertificationButton)

        // Initialize repository
        certificationRepository = CertificationRepository(requireContext())

        // Set click listener for the save button
        saveCertificationButton.setOnClickListener {
            saveCertification()
        }

        return view
    }

    private fun saveCertification() {
        val name = certificationNameEditText.text.toString()
        val date = certificationDateEditText.text.toString()
        val agency = certificationAgencyEditText.text.toString()

        if (name.isBlank() || date.isBlank() || agency.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new certification object
        val certification = Certification(name, date, agency)

        // Save the certification to the repository
        certificationRepository.addCertification(certification)

        Toast.makeText(requireContext(), "Certification added", Toast.LENGTH_SHORT).show()

        // Optionally, navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }
}
