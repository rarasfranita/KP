package com.example.lotus.models

data class Dm(
    val _id: String,
    val lastMessage: LastMessage,
    val name: String,
    val participants: List<String>,
    val participantsCount: Int,
    val `receiver`: Receiver,
    val type: String
)