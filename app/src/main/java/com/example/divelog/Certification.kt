package com.example.divelog

data class Certification(
    val name: String,         // Certification name (e.g., "Open Water Diver")
    val organization: String, // Organization that issued the certification (e.g., "PADI")
    val year: String,         // Year the certification was received (e.g., "2023")
    val imageUri: String?     // Optional image URI for the certification (e.g., path to image)
)
