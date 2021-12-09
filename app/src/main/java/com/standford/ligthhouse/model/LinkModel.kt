package com.standford.ligthhouse.model

import org.json.JSONObject
import java.util.*

data class LinkModel(
    var sender: String,
    var link: String,
    var originalMessage: String,
    var identifier: String,
    var topline: String,
    var rank: String,
    var score: Int,
    var writeup: JSONObject,
    val messagesContainingDomain: ArrayList<MessageModel> = ArrayList()
)