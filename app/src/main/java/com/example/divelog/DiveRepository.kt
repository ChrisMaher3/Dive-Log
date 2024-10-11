package com.example.divelog

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DiveRepository(context: Context) {

    private val dbHelper = DiveDatabaseHelper(context)

    fun addDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DiveDatabaseHelper.COLUMN_LOCATION, dive.location)
            put(DiveDatabaseHelper.COLUMN_MAX_DEPTH, dive.maxDepth)
            put(DiveDatabaseHelper.COLUMN_DURATION, dive.duration)
        }
        db.insert(DiveDatabaseHelper.TABLE_DIVES, null, values)
        db.close()
    }

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
                dives.add(Dive(location, maxDepth, duration))
            }
        }

        cursor.close()
        db.close()
        return dives
    }

    // Optional: Add method to delete a dive
    fun deleteDive(dive: Dive) {
        val db = dbHelper.writableDatabase
        db.delete(DiveDatabaseHelper.TABLE_DIVES, "${DiveDatabaseHelper.COLUMN_LOCATION} = ?", arrayOf(dive.location))
        db.close()
    }
}
