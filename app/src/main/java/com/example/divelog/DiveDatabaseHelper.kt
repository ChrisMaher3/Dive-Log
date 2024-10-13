package com.example.divelog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DiveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "dive_log.db" // Renamed for clarity
        const val DATABASE_VERSION = 1

        // Dive table
        const val TABLE_DIVES = "dives"
        const val COLUMN_DIVE_ID = "_id"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_MAX_DEPTH = "max_depth"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATE = "date" // Add date column

        // Certification table
        const val TABLE_CERTIFICATIONS = "certifications"
        const val COLUMN_CERTIFICATION_ID = "certification_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ORGANIZATION = "organization"
        const val COLUMN_YEAR = "year"
        const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create dives table
        val CREATE_DIVES_TABLE = ("CREATE TABLE $TABLE_DIVES ("
                + "$COLUMN_DIVE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_LOCATION TEXT,"
                + "$COLUMN_MAX_DEPTH REAL,"
                + "$COLUMN_DURATION INTEGER,"
                + "$COLUMN_DATE TEXT)") // Create column for the date
        db.execSQL(CREATE_DIVES_TABLE)

        // Create certifications table
        val CREATE_CERTIFICATIONS_TABLE = ("CREATE TABLE $TABLE_CERTIFICATIONS ("
                + "$COLUMN_CERTIFICATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_ORGANIZATION TEXT,"
                + "$COLUMN_YEAR TEXT,"
                + "$COLUMN_IMAGE_URI TEXT)")
        db.execSQL(CREATE_CERTIFICATIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CERTIFICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIVES")
        onCreate(db)
    }

    // Certification-related functions
    fun addCertification(certification: Certification) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, certification.name)
            put(COLUMN_ORGANIZATION, certification.organization)
            put(COLUMN_YEAR, certification.year)
            put(COLUMN_IMAGE_URI, certification.imageUri)
        }
        db.insert(TABLE_CERTIFICATIONS, null, values)
        db.close()
    }

    fun getAllCertifications(): List<Certification> {
        val certifications = mutableListOf<Certification>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CERTIFICATIONS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                // Get the column indices and check for their existence
                val idIndex = cursor.getColumnIndex(COLUMN_CERTIFICATION_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val organizationIndex = cursor.getColumnIndex(COLUMN_ORGANIZATION)
                val yearIndex = cursor.getColumnIndex(COLUMN_YEAR)
                val imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI)

                if (idIndex != -1 && nameIndex != -1 && organizationIndex != -1 && yearIndex != -1 && imageUriIndex != -1) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex)
                    val organization = cursor.getString(organizationIndex)
                    val year = cursor.getString(yearIndex)
                    val imageUri = cursor.getString(imageUriIndex)

                    certifications.add(Certification(id, name, organization, year, imageUri))
                } else {
                    // Handle the case where one of the columns does not exist
                    Log.e("DatabaseError", "One or more columns do not exist in the certifications table")
                }

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return certifications
    }

}
