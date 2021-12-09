package com.standford.ligthhouse.model

import org.json.JSONObject


data class NewsguardModel(
    val createdDate: String? = null,
    val id: String? = null,
    var profileId: String? = null,
    var identifier: String? = null,
    var topline: String? = null,
    var rank: String? = null,
    val score: Int = 0,
    var country: String? = null,
    var language: String? = null,
    var writeup: JSONObject? = null,
    var criteria: JSONObject? = null,
    val active: Boolean = false,
    val healthGuard: Boolean = false,
    val locale: String? = null

)