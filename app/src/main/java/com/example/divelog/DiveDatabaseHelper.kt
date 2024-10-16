package com.example.divelog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DiveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "dive_log.db" // Database name
        const val DATABASE_VERSION = 1

        // Dives table definition
        const val TABLE_DIVES = "dives"
        const val COLUMN_DIVE_ID = "_id"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_MAX_DEPTH = "max_depth"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATE = "date" // Date column

        // Certifications table definition
        const val TABLE_CERTIFICATIONS = "certifications"
        const val COLUMN_CERTIFICATION_ID = "certification_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ORGANIZATION = "organization"
        const val COLUMN_YEAR = "year"
        const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // SQL to create the dives table
        val CREATE_DIVES_TABLE = """
            CREATE TABLE $TABLE_DIVES (
                $COLUMN_DIVE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_MAX_DEPTH REAL,
                $COLUMN_DURATION INTEGER,
                $COLUMN_DATE TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_DIVES_TABLE)

        // SQL to create the certifications table
        val CREATE_CERTIFICATIONS_TABLE = """
            CREATE TABLE $TABLE_CERTIFICATIONS (
                $COLUMN_CERTIFICATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_ORGANIZATION TEXT,
                $COLUMN_YEAR TEXT,
                $COLUMN_IMAGE_URI TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_CERTIFICATIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop old tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIVES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CERTIFICATIONS")
        // Recreate tables
        onCreate(db)
    }

    // -------------------- Certification-related functions ------------------------

    // Function to add a new certification
    fun addCertification(certification: Certification) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, certification.name)
            put(COLUMN_ORGANIZATION, certification.organization)
            put(COLUMN_YEAR, certification.year)
            put(COLUMN_IMAGE_URI, certification.imageUri) // Can be null
        }
        // Insert the new certification into the database
        db.insert(TABLE_CERTIFICATIONS, null, values)
        db.close()
    }

    // Function to retrieve all certifications
    fun getAllCertifications(): List<Certification> {
        val certifications = mutableListOf<Certification>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CERTIFICATIONS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                // Get the column indices
                val idIndex = cursor.getColumnIndex(COLUMN_CERTIFICATION_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val organizationIndex = cursor.getColumnIndex(COLUMN_ORGANIZATION)
                val yearIndex = cursor.getColumnIndex(COLUMN_YEAR)
                val imageUriIndex = cursor.getColumnIndex(COLUMN_IMAGE_URI)

                // Check that indices are valid and fetch data
                if (idIndex != -1 && nameIndex != -1 && organizationIndex != -1 && yearIndex != -1) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex)
                    val organization = cursor.getString(organizationIndex)
                    val year = cursor.getString(yearIndex)
                    // Use Elvis operator to provide a default value if imageUri is null
                    val imageUri = if (imageUriIndex != -1) cursor.getString(imageUriIndex) else null

                    certifications.add(Certification(id, name, organization, year, imageUri ?: "No Image Available"))
                } else {
                    Log.e("DatabaseError", "One or more columns do not exist in the certifications table")
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return certifications
    }

    // Function to delete a certification by its attributes (name, organization, year)
    fun deleteCertification(certification: Certification) {
        val db = writableDatabase

        // Execute delete query using attributes from the certification object
        val rowsDeleted = db.delete(
            TABLE_CERTIFICATIONS,
            "$COLUMN_NAME = ? AND $COLUMN_ORGANIZATION = ? AND $COLUMN_YEAR = ?",
            arrayOf(certification.name, certification.organization, certification.year)
        )

        // Log the number of rows deleted for debugging purposes
        Log.d("Database", "$rowsDeleted certification(s) deleted.")

        db.close()
    }

    // ------------------- Dive Log-related functions ------------------------

    // Function to add a dive log (you can expand this as needed)
    fun addDive(location: String, maxDepth: Double, duration: Int, date: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION, location)
            put(COLUMN_MAX_DEPTH, maxDepth)
            put(COLUMN_DURATION, duration)
            put(COLUMN_DATE, date)
        }
        db.insert(TABLE_DIVES, null, values)
        db.close()
    }

    // Additional dive-related functions can go here...
}
