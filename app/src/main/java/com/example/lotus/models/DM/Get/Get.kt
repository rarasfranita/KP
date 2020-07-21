package com.example.lotus.models.DM.Get

import com.example.lotus.models.DM.Channel.ReceiverX
import com.example.lotus.models.DM.Send.Sender

data class Get(
    val __v: Int?,
    val _id: String?,
    val channelId: String?,
    val createdAt: String?,
    val deleted: Boolean?,
    val isRead: Boolean?,
    val receiver: ReceiverX?,
    val sender: Sender?,
    val text: String?,
    val updatedAt: String?,
    var mine:Int?
)
