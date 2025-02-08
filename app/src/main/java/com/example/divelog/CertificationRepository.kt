package com.chris.divelog

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CertificationRepository(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("certifications", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addCertification(certification: Certification) {
        val certifications = getAllCertifications().toMutableList()
        certifications.add(certification)
        saveCertifications(certifications)
    }

    fun getAllCertifications(): List<Certification> {
        val json = sharedPref.getString("certifications_list", null)
        return if (json != null) {
            val type = object : TypeToken<List<Certification>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun saveCertifications(certifications: List<Certification>) {
        val json = gson.toJson(certifications)
        with(sharedPref.edit()) {
            putString("certifications_list", json)
            apply()
        }
    }
}
