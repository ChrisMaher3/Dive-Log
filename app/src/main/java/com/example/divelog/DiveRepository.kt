package com.example.divelog

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DiveRepository(context: Context) {

    private val dbHelper = DiveDatabaseHelper(context)

    // Method to add a new dive to the database
    fun addDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DiveDatabaseHelper.COLUMN_LOCATION, dive.location)
            put(DiveDatabaseHelper.COLUMN_MAX_DEPTH, dive.maxDepth)
            put(DiveDatabaseHelper.COLUMN_DURATION, dive.duration)
            put(DiveDatabaseHelper.COLUMN_DATE, dive.date) // Add date column for the dive date
        }
        db.insert(DiveDatabaseHelper.TABLE_DIVES, null, values)
        db.close()
    }

    // Method to retrieve all dives from the database
    fun getAllDives(): List<Dive> {
        val dives = mutableListOf<Dive>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DiveDatabaseHelper.TABLE_DIVES,
            null, null, null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val location = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_LOCATION))
                val maxDepth = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_MAX_DEPTH))
                val duration = getInt(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DURATION))
                val date = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DATE)) // Retrieve the date
                dives.add(Dive(location, maxDepth, duration, date)) // Create Dive object with date
            }
        }
        cursor.close()
        db.close()
        return dives
    }

    // Optional method to delete a dive
    fun deleteDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        db.delete(DiveDatabaseHelper.TABLE_DIVES, "${DiveDatabaseHelper.COLUMN_LOCATION} = ?", arrayOf(dive.location))
        db.close()
    }
}
