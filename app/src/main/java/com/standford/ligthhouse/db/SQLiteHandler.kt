package com.standford.ligthhouse.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sQLiteDatabase: SQLiteDatabase) {
        sQLiteDatabase.execSQL(
            "create table api_data_STLIGHT (" +
                    "data_id integer primary key autoincrement," +
                    "createdDate text," +
                    "id text," +
                    "profileId text," +
                    "identifier text," +
                    "topline text," +
                    "rank text," +
                    "score integer," +
                    "country text," +
                    "language text," +
                    "writeup text," +
                    "criteria text," +
                    "active text," +
                    "healthGuard text," +
                    "locale text" +
                    ")"
        )

        sQLiteDatabase.execSQL(
            "create table body_list (" +
                    "body_id integer primary key autoincrement," +
                    "profileId text," +
                    "title text," +
                    "body text," +
                    "body_order integer" +
                    ")"
        )

    }

    override fun onUpgrade(sQLiteDatabase: SQLiteDatabase, i: Int, i2: Int) {

    }

    companion object {
        const val DATABASE_NAME = "lighhousenew.db"
        private const val DATABASE_VERSION = 1
    }
}