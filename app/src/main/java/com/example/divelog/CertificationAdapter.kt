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

        // Use existing view or inflate a new one
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_certification, parent, false)

        // Find and set views
        val nameTextView: TextView = listItemView.findViewById(R.id.certificationName)
        val agencyTextView: TextView = listItemView.findViewById(R.id.certificationAgency)
        val dateTextView: TextView = listItemView.findViewById(R.id.certificationDate)

        nameTextView.text = certification.name
        agencyTextView.text = certification.agency
        dateTextView.text = certification.date

        return listItemView
    }
}
