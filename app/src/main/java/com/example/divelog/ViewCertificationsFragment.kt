package com.example.divelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewCertificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CertificationAdapter
    private lateinit var certifications: List<Certification>
    private lateinit var addCertificationButton: Button
    private lateinit var dbHelper: DiveDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_certifications, container, false)

        recyclerView = view.findViewById(R.id.certificationRecyclerView)
        addCertificationButton = view.findViewById(R.id.addCertificationButton)

        dbHelper = DiveDatabaseHelper(requireContext())

        loadCertifications()

        adapter = CertificationAdapter(certifications, requireContext()) { certification ->
            val dialog = DeleteConfirmationDialogFragment.newInstance(certification)
            dialog.show(parentFragmentManager, "DeleteConfirmationDialogFragment")
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        addCertificationButton.setOnClickListener {
            val fragment = AddCertificationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    fun loadCertifications() {
        certifications = dbHelper.getAllCertifications()
        if (::adapter.isInitialized) {
            adapter.updateData(certifications)
        }
    }
}
