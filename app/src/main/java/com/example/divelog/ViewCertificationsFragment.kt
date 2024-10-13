package com.example.divelog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment

class ViewCertificationsFragment : Fragment() {

    private lateinit var certificationsListView: ListView
    private lateinit var addCertificationButton: Button
    private var certifications: MutableList<Certification> = mutableListOf()
    private lateinit var adapter: CertificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadCertifications() // Load initial certifications
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_certifications, container, false)

        // Initialize views
        certificationsListView = view.findViewById(R.id.certificationsListView)
        addCertificationButton = view.findViewById(R.id.addCertificationButton)

        // Set up the adapter for the ListView
        adapter = CertificationAdapter(requireContext(), certifications)
        certificationsListView.adapter = adapter

        // Set click listener for the add certification button
        addCertificationButton.setOnClickListener {
            // Navigate to AddCertificationFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddCertificationFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun loadCertifications() {
        // Sample certifications for testing (you may want to replace this with actual data)
        certifications.add(Certification("Open Water Diver", "PADI", "2022", null))
        certifications.add(Certification("Advanced Open Water Diver", "PADI", "2023", null))
    }

    // Function to refresh the list of certifications
    fun addCertification(certification: Certification) {
        certifications.add(certification)
        adapter.notifyDataSetChanged() // Notify the adapter to refresh the ListView
    }
}
