package com.example.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CertificationAdapter(context: Context, private val certifications: List<Certification>) :
    ArrayAdapter<Certification>(context, 0, certifications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val certification = certifications[position]

        // Check if an existing view is being reused, otherwise inflate the view
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_certification, parent, false)

        // Lookup view for data population
        val nameTextView = listItemView.findViewById<TextView>(R.id.certificationNameTextView)
        val organizationTextView = listItemView.findViewById<TextView>(R.id.certificationOrganizationTextView)
        val yearTextView = listItemView.findViewById<TextView>(R.id.certificationYearTextView)

        // Populate the data into the template view using the data object
        nameTextView.text = certification.name
        organizationTextView.text = certification.organization
        yearTextView.text = certification.year

        // Return the completed view to render on screen
        return listItemView
    }
}
