package com.standford.ligthhouse.model

import java.util.*

data class LinkModel(
    var sender: String,
    var link: String,
    var originalMessage: String,
    var identifier: String,
    var topline: String,
    var rank: String,
    var score: Int,
    var writeup: List<BodyList>?,
    val messagesContainingDomain: ArrayList<MessageModel> = ArrayList()
)