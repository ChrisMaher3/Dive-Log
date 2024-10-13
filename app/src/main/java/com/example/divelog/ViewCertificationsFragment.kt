package com.example.divelog

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
        loadCertifications()
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
            // Logic to add certification (you may want to start a new fragment or activity)
        }

        return view
    }

    private fun loadCertifications() {
        // Sample certifications for testing
        certifications.add(Certification("Open Water Diver", "PADI", "2022"))
        certifications.add(Certification("Advanced Open Water Diver", "PADI", "2023"))
    }
}
