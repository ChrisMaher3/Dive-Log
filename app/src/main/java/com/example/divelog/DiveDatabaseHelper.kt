package com.example.divelog

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DiveDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "diveLog.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_DIVES = "dives"
        const val COLUMN_ID = "_id"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_MAX_DEPTH = "max_depth"
        const val COLUMN_DURATION = "duration"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_DIVES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_MAX_DEPTH + " REAL, "
                + COLUMN_DURATION + " INTEGER" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIVES")
        onCreate(db)
    }
}
