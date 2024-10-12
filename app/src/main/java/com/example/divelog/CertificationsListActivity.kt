package com.example.divelog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CertificationsListActivity : AppCompatActivity() {

    private lateinit var certificationsContainer: LinearLayout
    private lateinit var addCertificationButton: Button

    // Sample list to store certifications in memory
    private val certifications = mutableListOf<Certification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certifications_list)

        certificationsContainer = findViewById(R.id.certificationsContainer)
        addCertificationButton = findViewById(R.id.addCertificationButton)

        // Load certifications on startup
        loadCertifications()

        // Set up button to add new certifications
        addCertificationButton.setOnClickListener {
            val intent = Intent(this, CertificationsActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    // Load certifications into the UI
    private fun loadCertifications() {
        certificationsContainer.removeAllViews() // Clear existing views

        for (certification in certifications) {
            val certificationView = layoutInflater.inflate(R.layout.certification_item, certificationsContainer, false)
            val titleTextView: TextView = certificationView.findViewById(R.id.titleTextView)
            val dateTextView: TextView = certificationView.findViewById(R.id.dateTextView)

            titleTextView.text = certification.title
            dateTextView.text = certification.date

            certificationsContainer.addView(certificationView)
        }
    }

    // Add a new certification
    fun addCertification(certification: Certification) {
        certifications.add(certification)
        loadCertifications()
    }

    // Handle result from CertificationsActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val certification: Certification? = data?.getParcelableExtra("certification")
            certification?.let {
                addCertification(it) // Add to the list
            }
        }
    }
}
