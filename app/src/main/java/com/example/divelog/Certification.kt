package com.example.divelog

data class Certification(
    val id: Long = 0, // ID for the database
    val name: String,
    val organization: String,
    val year: String,
    val imageUri: String? // Store the URI of the image
)
