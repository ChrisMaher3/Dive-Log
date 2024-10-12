package com.example.divelog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Certification(
    val title: String,
    val date: String,
    val imageUri: String // Store image URI as a String
) : Parcelable
