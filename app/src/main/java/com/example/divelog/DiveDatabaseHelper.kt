package com.example.divelog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DiveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "dive_log.db" // Database name
        const val DATABASE_VERSION = 2 // Incremented version to handle upgrades

        // Dives table definition
        const val TABLE_DIVES = "dives"
        const val COLUMN_DIVE_ID = "_id"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_MAX_DEPTH = "max_depth"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATE = "date" // Date column
        const val COLUMN_BUDDY = "buddy" // New column for buddy name
        const val COLUMN_WEATHER_CONDITIONS = "weather_conditions" // New column for weather conditions
        const val COLUMN_VISIBILITY = "visibility" // New column for visibility
        const val COLUMN_IS_NIGHT_DIVE = "is_night_dive" // New column for night dive status

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
                $COLUMN_DATE TEXT,
                $COLUMN_BUDDY TEXT,                     
                $COLUMN_WEATHER_CONDITIONS TEXT,       
                $COLUMN_VISIBILITY REAL,                
                $COLUMN_IS_NIGHT_DIVE INTEGER NOT NULL DEFAULT 0 
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

    // Function to add a dive log
    fun addDive(dive: Dive) { // Expecting a Dive object
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION, dive.location)
            put(COLUMN_MAX_DEPTH, dive.maxDepth)
            put(COLUMN_DURATION, dive.duration)
            put(COLUMN_DATE, dive.date)
            put(COLUMN_BUDDY, dive.diveBuddy) // New property
            put(COLUMN_WEATHER_CONDITIONS, dive.weatherConditions) // New property
            put(COLUMN_VISIBILITY, dive.visibility) // New property
            put(COLUMN_IS_NIGHT_DIVE, if (dive.isNightDive) 1 else 0) // New property
        }
        db.insert(TABLE_DIVES, null, values)
        db.close()
    }

    // Function to retrieve all dives
    fun getAllDives(): List<Dive> {
        val dives = mutableListOf<Dive>()
        val db = readableDatabase
        val cursor = db.query(TABLE_DIVES, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                // Get the column indices
                val locationIndex = cursor.getColumnIndex(COLUMN_LOCATION)
                val maxDepthIndex = cursor.getColumnIndex(COLUMN_MAX_DEPTH)
                val durationIndex = cursor.getColumnIndex(COLUMN_DURATION)
                val dateIndex = cursor.getColumnIndex(COLUMN_DATE)
                val buddyIndex = cursor.getColumnIndex(COLUMN_BUDDY)
                val weatherConditionsIndex = cursor.getColumnIndex(COLUMN_WEATHER_CONDITIONS)
                val visibilityIndex = cursor.getColumnIndex(COLUMN_VISIBILITY)
                val isNightDiveIndex = cursor.getColumnIndex(COLUMN_IS_NIGHT_DIVE)

                // Check that indices are valid and fetch data
                if (locationIndex != -1 && maxDepthIndex != -1 && durationIndex != -1 && dateIndex != -1) {
                    val location = cursor.getString(locationIndex)
                    val maxDepth = cursor.getFloat(maxDepthIndex)
                    val duration = cursor.getInt(durationIndex)
                    val date = cursor.getString(dateIndex)
                    val buddy = cursor.getString(buddyIndex) // New property
                    val weatherConditions = cursor.getString(weatherConditionsIndex) // New property
                    val visibility = cursor.getFloat(visibilityIndex) // New property
                    val isNightDive = cursor.getInt(isNightDiveIndex) == 1 // New property

                    dives.add(Dive(location, maxDepth, duration, date, buddy, weatherConditions, visibility, isNightDive))
                } else {
                    Log.e("DatabaseError", "One or more columns do not exist in the dives table")
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return dives
    }

    // Additional dive-related functions can go here...
}
