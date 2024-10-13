package com.example.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CertificationAdapter(
    context: Context,
    private val certifications: MutableList<Certification>
) : ArrayAdapter<Certification>(context, 0, certifications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        val certification = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_certification, parent, false)

        // Lookup view for data population
        val certificationNameTextView: TextView = view.findViewById(R.id.certificationNameTextView)
        val organizationTextView: TextView = view.findViewById(R.id.organizationTextView)
        val yearTextView: TextView = view.findViewById(R.id.yearTextView)
        val certificationImageView: ImageView = view.findViewById(R.id.certificationImageView)

        // Populate the data into the template view using the data object
        certificationNameTextView.text = certification?.name
        organizationTextView.text = certification?.organization
        yearTextView.text = certification?.year

        // Here you would typically set the image from a URI or resource
        // For demonstration, we will set a placeholder image
        // Replace this with actual image loading code if needed
        certificationImageView.setImageResource(R.drawable.cert) // Set your placeholder image

        // Return the completed view to render on screen
        return view
    }
}
