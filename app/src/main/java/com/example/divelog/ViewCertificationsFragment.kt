package com.example.divelog

import android.os.Bundle
import android.util.Log
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
    private lateinit var databaseHelper: DiveDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DiveDatabaseHelper(requireContext()) // Initialize the database helper
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_certifications, container, false)

        certificationsListView = view.findViewById(R.id.certificationsListView)
        addCertificationButton = view.findViewById(R.id.addCertificationButton)

        adapter = CertificationAdapter(requireContext(), certifications)
        certificationsListView.adapter = adapter

        addCertificationButton.setOnClickListener {
            // Navigate to AddCertificationFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddCertificationFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCertifications() // Load certifications after view is created
    }

    internal fun loadCertifications() {
        try {
            certifications.clear() // Clear the list before loading
            certifications.addAll(databaseHelper.getAllCertifications()) // Retrieve certifications from the database
            adapter.notifyDataSetChanged() // Notify adapter of data change
        } catch (e: Exception) {
            Log.e("ViewCertFrag", "Error loading certifications: ${e.message}")
            // Optionally, show an error message to the user
        }
    }
}
