package com.standford.ligthhouse.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.standford.ligthhouse.model.BodyList
import com.standford.ligthhouse.model.Data
import com.standford.ligthhouse.model.LinkModel
import com.standford.ligthhouse.model.MessageModel
import org.json.JSONObject
import java.util.*


class SQLiteHelper(context: Context?) {

    var database: SQLiteDatabase? = null
    var dbHandler: SQLiteHandler

    fun open() {
        database = dbHandler.writableDatabase
    }

    fun close() {
        val sQLiteDatabase = database
        if (sQLiteDatabase != null && sQLiteDatabase.isOpen) {
            dbHandler.close()
        }
    }

    fun stuffInsert(
        createdDate: String?,
        id: String?,
        profileId: String?,
        identifier: String?,
        topline: String?,
        rank: String?,
        score: String?,
        country: String?,
        language: String?,
        writeup: String?,
        criteria: String?,
        active: Boolean?,
        healthGuard: Boolean?,
        locale: String?
    ) {
        Log.e("database.insert:id:", id.toString())
        val contentValues = ContentValues()
        contentValues.put("createdDate", createdDate)
        contentValues.put("data_id", id)
        contentValues.put("profileId", profileId)
        contentValues.put("identifier", identifier)
        contentValues.put("topline", topline)
        contentValues.put("rank", rank)
        contentValues.put("score", score)
        contentValues.put("country", country)
        contentValues.put("language", language)
        contentValues.put("writeup", writeup)
        contentValues.put("criteria", criteria)
        contentValues.put("active", active)
        contentValues.put("healthGuard", healthGuard)
        contentValues.put("locale", locale)
        database!!.insert("api_data_STLIGHT", null, contentValues)
    }

    fun namelinkInsert(
        sender: String?,
        link: String?,
        original_message: String?
    ) {
        val contentValues = ContentValues()
        contentValues.put("sender", sender)
        contentValues.put("identifier", link)
        contentValues.put("originalMessage", original_message)
        database!!.insert("name_link", null, contentValues)
    }

    fun stuffInsertMessage(
        linkModel: LinkModel
    ) {
        val gson = Gson()

        val contentValues = ContentValues()
        contentValues.put("sender", linkModel.sender)
        contentValues.put("link", linkModel.link)
        contentValues.put("originalMessage", linkModel.originalMessage)
        contentValues.put("identifier", linkModel.identifier)
        contentValues.put("topline", linkModel.topline)
        contentValues.put("rank", linkModel.rank)
        contentValues.put("score", linkModel.score)
        contentValues.put("writeup", gson.toJson(linkModel.writeup))
        contentValues.put(
            "messagesContainingDomain",
            gson.toJson(linkModel.messagesContainingDomain)
        )
        database!!.insert("api_data_STLIGHT_message", null, contentValues)
    }


    fun bodyInsert(
        profileId: String?,
        title: String?,
        body: String?,
        order: Int?
    ) {
        val contentValues = ContentValues()
        contentValues.put("profileId", profileId)
        contentValues.put("title", title)
        contentValues.put("body", body)
        contentValues.put("body_order", order)
        database!!.insert("body_list", null, contentValues)
    }

    fun stuffContentInsert(contentValues: ContentValues?) {
        val sb = StringBuilder()
        sb.append("")
        sb.append(database!!.insert("api_data_STLIGHT", null, contentValues))
        Log.e("database.insert::", sb.toString())
    }

    fun bodyContentInsert(contentValues: ContentValues?) {
        val sb = StringBuilder()
        sb.append("")
        sb.append(database!!.insert("body_list", null, contentValues))
        Log.e("database.insert::", sb.toString())
    }

    fun stuffUpdate(
        createdDate: String?,
        id: String?,
        profileId: String?,
        identifier: String?,
        topline: String?,
        rank: String?,
        score: Int?,
        country: String?,
        language: String?,
        writeup: List<BodyList>?,
        criteria: String?,
        active: Boolean?,
        healthGuard: Boolean?,
        locale: String?
    ) {
        val contentValues = ContentValues()
        contentValues.put("createdDate", createdDate)
        contentValues.put("data_id", id)
        contentValues.put("profileId", profileId)
        contentValues.put("identifier", identifier)
        contentValues.put("topline", topline)
        contentValues.put("rank", rank)
        contentValues.put("score", score)
        contentValues.put("country", country)
        contentValues.put("language", language)
//        contentValues.put("writeup", writeup)
        contentValues.put("criteria", criteria)
        contentValues.put("active", active)
        contentValues.put("healthGuard", healthGuard)
        contentValues.put("locale", locale)
        database!!.update(
            "api_data_STLIGHT",
            contentValues,
            "id = ?",
            arrayOf(id.toString())
        )
        for (bodylist in writeup!!) {
            bodyUpdate(profileId, bodylist.title, bodylist.body.toString(), bodylist.order)
        }
    }

    fun bodyUpdate(
        profileId: String?,
        title: String?,
        body: String?,
        order: Int?
    ) {
        val contentValues = ContentValues()
        contentValues.put("profileId", profileId)
        contentValues.put("title", title)
        contentValues.put("body", body)
        contentValues.put("body_order", order)
        database!!.update(
            "body_list",
            contentValues,
            "profileId = ?",
            arrayOf(profileId.toString())
        )
        database!!.insert("body_list", null, contentValues)
    }

    fun isTableExists(str: String?): Boolean {
        val sQLiteDatabase = database
        val sb = StringBuilder()
        sb.append("select DISTINCT tbl_name from sqlite_master where tbl_name = '")
        sb.append(str)
        sb.append("'")
        val rawQuery = sQLiteDatabase!!.rawQuery(sb.toString(), null)
        if (rawQuery != null) {
            if (rawQuery.count > 0) {
                rawQuery.close()
                return true
            }
            rawQuery.close()
        }
        return false
    }

    fun dropOldTable(str: String?): Boolean {
        val sQLiteDatabase = database
        val sb = StringBuilder()
        sb.append("DROP TABLE ")
        sb.append(str)
        val rawQuery = sQLiteDatabase!!.rawQuery(sb.toString(), null)
        if (rawQuery != null) {
            if (rawQuery.count > 0) {
                rawQuery.close()
                return true
            }
            rawQuery.close()
        }
        return false
    }

    fun bitMapToString(bArr: ByteArray?): String? {
        return if (bArr != null) {
            Base64.encodeToString(bArr, 0)
        } else null
    }

    fun truncateAll() {
        database!!.execSQL("DELETE FROM api_data_STLIGHT")
    }

    @SuppressLint("Range")
    fun getData(context: Context?, identifier: String): Data? {
        Log.e("getData", "str:" + identifier)
        val str2 = ""
        val stuffGetSet = Data()
        return try {
            val rawQuery = database!!.rawQuery(
                "SELECT * FROM api_data_STLIGHT where identifier = ?",
                arrayOf(identifier)
            )
            val sb = StringBuilder()
            sb.append(str2)
            sb.append(rawQuery.count)
            Log.e("cursor::01", sb.toString())
            val sb2 = StringBuilder()
            sb2.append(str2)
            sb2.append(rawQuery.count)
            Log.e("cursor::02", sb2.toString())
            if (rawQuery.moveToFirst()) {
                while (true) {
                    val sb3 = StringBuilder()
                    sb3.append(str2)
                    sb3.append(rawQuery.count)

//                    val data=Data()
//                    stuffGetSet = wit(
//                    stuffGetSet = wit(
                    stuffGetSet.createdDate =
                        rawQuery.getString(rawQuery.getColumnIndex("createdDate"))
                    stuffGetSet.id = rawQuery.getString(rawQuery.getColumnIndex("data_id"))
                    stuffGetSet.profileId =
                        rawQuery.getString(rawQuery.getColumnIndex("profileId"))
                    stuffGetSet.identifier =
                        rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                    stuffGetSet.topline = rawQuery.getString(rawQuery.getColumnIndex("topline"))
                    stuffGetSet.rank = rawQuery.getString(rawQuery.getColumnIndex("rank"))
                    stuffGetSet.score = rawQuery.getString(rawQuery.getColumnIndex("score"))
                    stuffGetSet.country = rawQuery.getString(rawQuery.getColumnIndex("country"))
                    stuffGetSet.language =
                        rawQuery.getString(rawQuery.getColumnIndex("language"))
//                        val str = rawQuery.getString(rawQuery.getColumnIndex("writeup"))
//                    data.list.forEach { sb.append(it).append(",") }
//                    data.list = str.split(",")

                    try {
                        stuffGetSet.writeup =
                            rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                    } catch (e: Exception) {
                        Log.e(
                            "EXCEPTION",
                            "getAllData: " + rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                        )
                        stuffGetSet.writeup =
                            rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                    }

                    try {
                        stuffGetSet.criteria =
                            JSONObject(rawQuery.getString(rawQuery.getColumnIndex("criteria")))
                    } catch (e: Exception) {
                        stuffGetSet.criteria =
                            "test"
                    }

                    stuffGetSet.active =
                        rawQuery.getString(rawQuery.getColumnIndex("active")).toBoolean()
                    stuffGetSet.healthGuard =
                        rawQuery.getString(rawQuery.getColumnIndex("healthGuard")).toBoolean()
                    stuffGetSet.locale = rawQuery.getString(rawQuery.getColumnIndex("locale"))

//                    )
                    try {
                        if (!rawQuery.moveToNext()) {
                            break
                        }
                    } catch (unused: SQLiteException) {
                        Log.e("getData", "SQLiteException01:" + unused)
                        return stuffGetSet
                    }
                }
                //StuffGetSet2 = stuffGetSet;
            }
            rawQuery.close()
            stuffGetSet
        } catch (unused2: SQLiteException) {
            Log.e("getData", "SQLiteException02:" + unused2)
            stuffGetSet
        }
    }

    @SuppressLint("Range")
    fun getAllnamelinkData(context: Context?, identifier: String): ArrayList<MessageModel> {
        val str = ""
        val arrayList = ArrayList<MessageModel>()
        try {
            val str2 = "SELECT * FROM name_link where identifier = ?"
            var rawQuery = database!!.rawQuery(str2, arrayOf(identifier))
            val sb = StringBuilder()
            sb.append(str)
            sb.append(rawQuery.count)
            val count = rawQuery.count
            for (i in 0..count) {
                val sQLiteDatabase = database
                rawQuery = database!!.rawQuery(str2 + " LIMIT 1 OFFSET " + i, null)

                if (rawQuery.moveToFirst()) {
                    do {

                        val stuffGetSet = MessageModel()

                        stuffGetSet.link = rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                        stuffGetSet.originalMessage =
                            rawQuery.getString(rawQuery.getColumnIndex("originalMessage"))
                        stuffGetSet.sender = rawQuery.getString(rawQuery.getColumnIndex("sender"))

                        arrayList.add(stuffGetSet)
                    } while (rawQuery.moveToNext())
                }
            }
            rawQuery.close()
            val sb5 = StringBuilder()
            sb5.append(str)
            sb5.append(arrayList.size)
        } catch (unused: Exception) {
            Log.e("EXCEPTION", "getAllnamelinkData: " + unused.message)
        }

        return arrayList
    }


    @SuppressLint("Range")
    fun getnamelinkData(context: Context?, identifier: String): MessageModel? {
        Log.e("getData", "str:" + identifier)
        val str2 = ""
        val stuffGetSet = MessageModel()
        return try {
            val rawQuery = database!!.rawQuery(
                "SELECT * FROM name_link where identifier = ?",
                arrayOf(identifier)
            )
            val sb = StringBuilder()
            sb.append(str2)
            sb.append(rawQuery.count)
            Log.e("cursor::01", sb.toString())
            val sb2 = StringBuilder()
            sb2.append(str2)
            sb2.append(rawQuery.count)
            Log.e("cursor::02", sb2.toString())
            if (rawQuery.moveToFirst()) {
                while (true) {
                    val sb3 = StringBuilder()
                    sb3.append(str2)
                    sb3.append(rawQuery.count)

                    stuffGetSet.link = rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                    stuffGetSet.originalMessage =
                        rawQuery.getString(rawQuery.getColumnIndex("originalMessage"))
                    stuffGetSet.sender = rawQuery.getString(rawQuery.getColumnIndex("createdDate"))

                    try {
                        if (!rawQuery.moveToNext()) {
                            break
                        }
                    } catch (unused: SQLiteException) {
                        Log.e("getData", "SQLiteException01:" + unused)
                        return stuffGetSet
                    }
                }
                //StuffGetSet2 = stuffGetSet;
            }
            rawQuery.close()
            stuffGetSet
        } catch (unused2: SQLiteException) {
            Log.e("getData", "SQLiteException02:" + unused2)
            stuffGetSet
        }
    }


    @SuppressLint("Range")
    fun getDataBody(context: Context?, profileId: String): List<BodyList>? {
        Log.e("getData", "str:" + profileId)
        val str2 = ""
        val writeup = ArrayList<BodyList>()
        return try {
            val rawQuery = database!!.rawQuery(
                "SELECT * FROM body_list where profileId = ?",
                arrayOf(profileId)
            )
            val sb = StringBuilder()
            sb.append(str2)
            sb.append(rawQuery.count)
            Log.e("cursor::01", sb.toString())
            val sb2 = StringBuilder()
            sb2.append(str2)
            sb2.append(rawQuery.count)
            Log.e("cursor::02", sb2.toString())
            if (rawQuery.moveToFirst()) {
                while (true) {
                    val sb3 = StringBuilder()
                    sb3.append(str2)
                    sb3.append(rawQuery.count)
                    val stuffGetSet = BodyList()
                    stuffGetSet.title = rawQuery.getString(rawQuery.getColumnIndex("title"))
                    stuffGetSet.body = listOf(rawQuery.getString(rawQuery.getColumnIndex("body")))
                    stuffGetSet.order = rawQuery.getInt(rawQuery.getColumnIndex("profileId"))
                    writeup.add(stuffGetSet)

                    try {
                        if (!rawQuery.moveToNext()) {
                            break
                        }
                    } catch (unused: SQLiteException) {
                        Log.e("getData", "SQLiteException01:" + unused)
                        return writeup
                    }
                }
                //StuffGetSet2 = stuffGetSet;
            }
            rawQuery.close()
            writeup
        } catch (unused2: SQLiteException) {
            Log.e("getData", "SQLiteException02:" + unused2)
            writeup
        }
    }

    @SuppressLint("Range")
    fun getAllSiteData(context: Context?): ArrayList<String> {
        val str = ""
        val arrayList = ArrayList<String>()
        try {
            val str2 = "SELECT identifier FROM api_data_STLIGHT"
            var rawQuery = database!!.rawQuery(str2, null)
            val sb = StringBuilder()
            sb.append(str)
            sb.append(rawQuery.count)
            val count = rawQuery.count
            for (i in 0..count) {
                val sQLiteDatabase = database
                rawQuery = database!!.rawQuery(str2 + " LIMIT 1 OFFSET " + i, null)

                if (rawQuery.moveToFirst()) {
                    do {
                        val stuffGetSet =
                            rawQuery.getString(rawQuery.getColumnIndex("identifier"))

                        arrayList.add(stuffGetSet)
                    } while (rawQuery.moveToNext())
                }
            }
            rawQuery.close()
            val sb5 = StringBuilder()
            sb5.append(str)
            sb5.append(arrayList.size)
        } catch (unused: SQLiteException) {
        }

        return arrayList
    }


    @SuppressLint("Range")
    fun getAllData(context: Context?): ArrayList<Data> {
        val str = ""
        val arrayList = ArrayList<Data>()
        try {
            val str2 = "SELECT * FROM api_data_STLIGHT"
            var rawQuery = database!!.rawQuery(str2, null)
            val sb = StringBuilder()
            sb.append(str)
            sb.append(rawQuery.count)
            val count = rawQuery.count
            for (i in 0..count) {
                val sQLiteDatabase = database
                rawQuery = database!!.rawQuery(str2 + " LIMIT 1 OFFSET " + i, null)

                if (rawQuery.moveToFirst()) {
                    do {
                        val stuffGetSet = Data()
//                    stuffGetSet = wit(
                        stuffGetSet.createdDate =
                            rawQuery.getString(rawQuery.getColumnIndex("createdDate"))
                        stuffGetSet.id = rawQuery.getString(rawQuery.getColumnIndex("data_id"))
                        stuffGetSet.profileId =
                            rawQuery.getString(rawQuery.getColumnIndex("profileId"))
                        stuffGetSet.identifier =
                            rawQuery.getString(rawQuery.getColumnIndex("identifier"))
                        stuffGetSet.topline = rawQuery.getString(rawQuery.getColumnIndex("topline"))
                        stuffGetSet.rank = rawQuery.getString(rawQuery.getColumnIndex("rank"))
                        stuffGetSet.score = rawQuery.getString(rawQuery.getColumnIndex("score"))
                        stuffGetSet.country = rawQuery.getString(rawQuery.getColumnIndex("country"))
                        stuffGetSet.language =
                            rawQuery.getString(rawQuery.getColumnIndex("language"))
//                        val str = rawQuery.getString(rawQuery.getColumnIndex("writeup"))
//                    data.list.forEach { sb.append(it).append(",") }
//                    data.list = str.split(",")

                        try {
                            stuffGetSet.writeup =
                                rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                        } catch (e: Exception) {
                            Log.e(
                                "EXCEPTION",
                                "getAllData: " + rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                            )
                            stuffGetSet.writeup =
                                rawQuery.getString(rawQuery.getColumnIndex("writeup"))
                        }

                        try {
                            stuffGetSet.criteria =
                                JSONObject(rawQuery.getString(rawQuery.getColumnIndex("criteria")))
                        } catch (e: Exception) {
                            stuffGetSet.criteria =
                                "test"
                        }

                        stuffGetSet.active =
                            rawQuery.getString(rawQuery.getColumnIndex("active")).toBoolean()
                        stuffGetSet.healthGuard =
                            rawQuery.getString(rawQuery.getColumnIndex("healthGuard")).toBoolean()
                        stuffGetSet.locale = rawQuery.getString(rawQuery.getColumnIndex("locale"))
                        arrayList.add(stuffGetSet)
                    } while (rawQuery.moveToNext())
                }
            }
            rawQuery.close()
            val sb5 = StringBuilder()
            sb5.append(str)
            sb5.append(arrayList.size)
        } catch (unused: SQLiteException) {
        }

        return arrayList
    }

    val recordCount: Int
        get() {
            val rawQuery =
                database!!.rawQuery("SELECT DISTINCT data_id FROM api_data_STLIGHT", null)
            rawQuery.moveToFirst()
            return rawQuery.count
        }

    fun stuffDelete(i: Int) {
        val deleteQuery = "DELETE FROM api_data_ST WHERE data_id='$i'"
        database!!.execSQL(deleteQuery)
    }

    companion object {
        private const val STUFF_ID = "id"
    }

    init {
        dbHandler = SQLiteHandler(context)
    }
}