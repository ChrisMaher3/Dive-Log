package com.example.divelog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CertificationAdapter(
    private var certifications: List<Certification>,
    private val context: Context,
    private val onItemClicked: (Certification) -> Unit
) : RecyclerView.Adapter<CertificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_certification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val certification = certifications[position]
        holder.nameTextView.text = certification.name
        holder.organizationTextView.text = certification.organization
        holder.yearTextView.text = certification.year

        // Load the image using Glide
        Glide.with(context)
            .load(certification.imageUri)
            .placeholder(R.drawable.cert) // Show a placeholder while loading
            .error(R.drawable.cert) // Show an error image if loading fails
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClicked(certification)
        }
    }

    override fun getItemCount(): Int = certifications.size

    fun updateData(newCertifications: List<Certification>) {
        certifications = newCertifications
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.certificationNameTextView)
        val organizationTextView: TextView = itemView.findViewById(R.id.certificationOrganizationTextView)
        val yearTextView: TextView = itemView.findViewById(R.id.certificationYearTextView)
        val imageView: ImageView = itemView.findViewById(R.id.certificationImageView)
    }
}
