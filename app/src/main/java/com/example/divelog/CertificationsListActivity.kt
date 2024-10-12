package com.example.divelog

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CertificationsListActivity : AppCompatActivity() {

    private lateinit var certificationsContainer: LinearLayout
    private lateinit var addCertificationButton: Button

    private val certifications = mutableListOf<Certification>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certifications_list)

        certificationsContainer = findViewById(R.id.certificationsContainer)
        addCertificationButton = findViewById(R.id.addCertificationButton)

        sharedPreferences = getSharedPreferences("certifications_prefs", MODE_PRIVATE)

        // Load certifications on startup
        loadCertifications()

        // Set up button to add new certifications
        addCertificationButton.setOnClickListener {
            val intent = Intent(this, CertificationsActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    // Load certifications from SharedPreferences into the UI
    private fun loadCertifications() {
        certificationsContainer.removeAllViews() // Clear existing views

        // Retrieve certifications from SharedPreferences
        val json = sharedPreferences.getString("certifications", null)
        if (json != null) {
            val type = object : TypeToken<List<Certification>>() {}.type
            val savedCertifications: List<Certification> = Gson().fromJson(json, type)
            certifications.addAll(savedCertifications)
        }

        for (certification in certifications) {
            val certificationView = layoutInflater.inflate(R.layout.certification_item, certificationsContainer, false)
            val titleTextView: TextView = certificationView.findViewById(R.id.titleTextView)
            val dateTextView: TextView = certificationView.findViewById(R.id.dateTextView)
            val imageView: ImageView = certificationView.findViewById(R.id.certificationImageView)

            titleTextView.text = certification.title
            dateTextView.text = certification.date

            // Load image from internal storage
            val file = File(certification.imageUri)
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file))
            } else {
                imageView.setImageResource(R.drawable.cert) // Or any placeholder image
            }

            certificationsContainer.addView(certificationView)
        }
    }

    // Add a new certification
    fun addCertification(certification: Certification) {
        certifications.add(certification)
        saveCertifications() // Save the certifications whenever one is added
        loadCertifications()
    }

    // Save certifications to SharedPreferences
    private fun saveCertifications() {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(certifications)
        editor.putString("certifications", json)
        editor.apply() // Apply changes asynchronously
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
