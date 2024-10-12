package com.example.divelog

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CertificationsActivity : AppCompatActivity() {

    private lateinit var certificateImageView: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certifications)

        certificateImageView = findViewById(R.id.certificateImageView)
        titleEditText = findViewById(R.id.titleEditText)
        dateEditText = findViewById(R.id.dateEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        uploadButton = findViewById(R.id.uploadButton)

        selectImageButton.setOnClickListener { openImageChooser() }
        uploadButton.setOnClickListener { saveCertification() }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Certificate Image"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            certificateImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveCertification() {
        val title = titleEditText.text.toString()
        val date = dateEditText.text.toString()

        // Save the selected image to internal storage
        val imageUriString = saveImageToInternalStorage(selectedImageUri)

        // Create Certification object
        val certification = Certification(title, date, imageUriString)

        // Add certification to the CertificationsListActivity
        val resultIntent = Intent().apply {
            putExtra("certification", certification)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun saveImageToInternalStorage(uri: Uri?): String {
        if (uri == null) return ""

        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val fileName = "${System.currentTimeMillis()}.jpg" // Generate a unique filename
        val file = File(filesDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath // Return the file path
    }
}
