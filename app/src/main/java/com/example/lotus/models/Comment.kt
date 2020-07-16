package com.example.lotus.models;

class Comment (
    val id: String?,
    val parentId: String?,
    val userId: String?,
    val text: String?,
    val username: String?,
    val profilePicture: String?,
    val createdAt: String?,
    val name: String?,
    var replies: MutableList<ChildComment>?
)

