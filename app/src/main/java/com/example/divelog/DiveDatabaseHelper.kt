package com.example.divelog

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DiveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "dives.db"
        const val DATABASE_VERSION = 1

        const val TABLE_DIVES = "dives"
        const val COLUMN_ID = "_id"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_MAX_DEPTH = "max_depth"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATE = "date" // Add date column
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_DIVES_TABLE = ("CREATE TABLE $TABLE_DIVES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_LOCATION TEXT,"
                + "$COLUMN_MAX_DEPTH REAL,"
                + "$COLUMN_DURATION INTEGER,"
                + "$COLUMN_DATE TEXT)") // Create column for the date
        db.execSQL(CREATE_DIVES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIVES")
        onCreate(db)
    }
}
