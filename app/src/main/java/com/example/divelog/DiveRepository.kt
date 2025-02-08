package com.chris.divelog

import android.content.ContentValues
import android.content.Context  // <-- Add this import statement
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.preference.PreferenceManager

class DiveRepository(context: Context) {
    private val dbHelper = DiveDatabaseHelper(context)

    // Function to add a dive log
    fun addDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DiveDatabaseHelper.COLUMN_LOCATION, dive.location)
            put(DiveDatabaseHelper.COLUMN_MAX_DEPTH, dive.maxDepth)
            put(DiveDatabaseHelper.COLUMN_DURATION, dive.duration)
            put(DiveDatabaseHelper.COLUMN_DATE, dive.date) // Save the date
            put(DiveDatabaseHelper.COLUMN_BUDDY, dive.diveBuddy) // Save the buddy name
            put(DiveDatabaseHelper.COLUMN_WEATHER_CONDITIONS, dive.weatherConditions) // Save weather conditions
            put(DiveDatabaseHelper.COLUMN_VISIBILITY, dive.visibility) // Save visibility
            put(DiveDatabaseHelper.COLUMN_WATER_TEMPERATURE, dive.waterTemperature) // Save water temperature
            put(DiveDatabaseHelper.COLUMN_IS_NIGHT_DIVE, if (dive.isNightDive) 1 else 0) // Save night dive status (1 for true, 0 for false)
        }
        db.insert(DiveDatabaseHelper.TABLE_DIVES, null, values)
        db.close()
    }

    // Function to retrieve all dives
    fun getAllDives(): List<Dive> {
        val dives = mutableListOf<Dive>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DiveDatabaseHelper.TABLE_DIVES,
            null, null, null, null, null, null
        )

        // Access the context here from dbHelper using the getter method
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dbHelper.getContext())
        val isMeters = sharedPreferences.getBoolean("isMeters", true)
        val isCelsius = sharedPreferences.getBoolean("isCelsius", true)

        with(cursor) {
            while (moveToNext()) {
                val location = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_LOCATION))
                var maxDepth = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_MAX_DEPTH))
                val duration = getInt(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DURATION))
                val date = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_DATE))
                val buddy = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_BUDDY))
                val weatherConditions = getString(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_WEATHER_CONDITIONS)) // Retrieve weather conditions
                val visibility = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_VISIBILITY)) // Retrieve visibility
                var waterTemperature = getFloat(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_WATER_TEMPERATURE)) // Retrieve water temperature
                val isNightDive = getInt(getColumnIndexOrThrow(DiveDatabaseHelper.COLUMN_IS_NIGHT_DIVE)) == 1 // Convert to Boolean

                // Apply conversions based on user's preference
                if (!isMeters) {
                    maxDepth = UnitConverter.metersToFeet(maxDepth) // Convert meters to feet if needed
                }

                if (!isCelsius) {
                    waterTemperature = UnitConverter.celsiusToFahrenheit(waterTemperature) // Convert Celsius to Fahrenheit if needed
                }

                dives.add(Dive(location, maxDepth, duration, date, buddy, weatherConditions, visibility, waterTemperature, isNightDive))
            }
        }
        cursor.close()
        db.close()
        return dives
    }

    // Function to delete a dive log
    fun deleteDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        db.delete(DiveDatabaseHelper.TABLE_DIVES, "${DiveDatabaseHelper.COLUMN_LOCATION} = ?", arrayOf(dive.location))
        db.close()
    }
}