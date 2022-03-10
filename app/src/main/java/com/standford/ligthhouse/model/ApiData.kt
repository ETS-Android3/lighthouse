package com.standford.ligthhouse.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class ApiData {
    @SerializedName("data")
    var data: dataObject? = null
}

class dataObject {
    @SerializedName("data")
    var data: List<Data>? = null
}

class Data {
    @SerializedName("createdDate")
    var createdDate: String? = null

    @SerializedName("jsonid")
    var id: String? = null

    @SerializedName("profileId")
    var profileId: String? = null

    @SerializedName("identifier")
    var identifier: String? = null
    var name: String? = null

    @SerializedName("topline")
    var topline: String? = null

    @SerializedName("rank")
    var rank: String? = null

    @SerializedName("score")
    var score = 0

    @SerializedName("country")
    var country: String? = null

    @SerializedName("language")
    var language: String? = null

    @SerializedName("criteria")
    var criteria: JSONObject? = null

    @SerializedName("active")
    var active = false

    @SerializedName("healthGuard")
    var healthGuard = false

    @SerializedName("locale")
    var locale: String? = null

    @SerializedName("writeup")
    var writeup: List<BodyList>? = null
}

class BodyList {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("body")
    var body: List<String>? = null

    @SerializedName("order")
    var order = 0
}

