package com.chris.divelog

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.preference.PreferenceManager

class DiveRepository(context: Context) {
    private val dbHelper = DiveDatabaseHelper(context)

    // Function to add a dive log
    fun addDive(dive: Dive): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DiveDatabaseHelper.COLUMN_LOCATION, dive.location)
            put(DiveDatabaseHelper.COLUMN_MAX_DEPTH, dive.maxDepth)
            put(DiveDatabaseHelper.COLUMN_DURATION, dive.duration)
            put(DiveDatabaseHelper.COLUMN_DATE, dive.date)
            put(DiveDatabaseHelper.COLUMN_BUDDY, dive.diveBuddy)
            put(DiveDatabaseHelper.COLUMN_WEATHER_CONDITIONS, dive.weatherConditions)
            put(DiveDatabaseHelper.COLUMN_VISIBILITY, dive.visibility)
            put(DiveDatabaseHelper.COLUMN_WATER_TEMPERATURE, dive.waterTemperature)
            put(DiveDatabaseHelper.COLUMN_IS_NIGHT_DIVE, if (dive.isNightDive) 1 else 0)
        }
        val id = db.insert(DiveDatabaseHelper.TABLE_DIVES, null, values)
        db.close()
        return id
    }

    // Function to retrieve all dives
    fun getAllDives(): List<Dive> {
        val dives = mutableListOf<Dive>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DiveDatabaseHelper.TABLE_DIVES,
            null, null, null, null, null, null
        )

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dbHelper.getContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true)
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true)

        with(cursor) {
            while (moveToNext()) {
                // Add ID retrieval here
                val id = getLong(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DIVE_ID))
                val location = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_LOCATION))
                var maxDepth = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_MAX_DEPTH))
                val duration = getInt(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DURATION))
                val date = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DATE))
                val buddy = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_BUDDY))
                val weatherConditions = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_WEATHER_CONDITIONS))
                val visibility = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_VISIBILITY))
                var waterTemperature = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_WATER_TEMPERATURE))
                val isNightDive = getInt(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_IS_NIGHT_DIVE)) == 1

                if (!isMeters) {
                    maxDepth = UnitConverter.metersToFeet(maxDepth)
                }

                if (!isCelsius) {
                    waterTemperature = UnitConverter.celsiusToFahrenheit(waterTemperature)
                }

                dives.add(
                    Dive(
                        id, // Now using the correct database ID
                        location,
                        maxDepth,
                        duration,
                        date,
                        buddy,
                        weatherConditions,
                        visibility,
                        waterTemperature,
                        isNightDive
                    )
                )
            }
        }
        cursor.close()
        db.close()
        return dives
    }

    // Function to delete a dive log
    fun deleteDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        // Fixed toString() typo and column name
        db.delete(
            DiveDatabaseHelper.TABLE_DIVES,
            "${DiveDatabaseHelper.COLUMN_DIVE_ID} = ?",
            arrayOf(dive.id.toString())
        )
        db.close()
    }
}