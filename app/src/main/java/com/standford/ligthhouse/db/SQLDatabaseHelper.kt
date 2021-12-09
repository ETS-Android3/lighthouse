package com.standford.ligthhouse.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class SQLDatabaseHelper(context: Context?) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val disinfoLinks: Cursor
        get() {
            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("domain")
            val sqlTables = "disinfo_links"
            qb.tables = sqlTables
            val c = qb.query(
                db, sqlSelect, null, null,
                null, null, null
            )
            c.moveToFirst()
            return c
        }

    companion object {
        private const val DATABASE_NAME = "disinfo_domains.db"
        private const val DATABASE_VERSION = 1
    }
}