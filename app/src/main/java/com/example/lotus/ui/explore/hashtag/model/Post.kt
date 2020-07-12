package com.example.lotus.ui.explore.hashtag.model

import com.example.lotus.models.MediaData

data class Post(
    val bio: String?,
    val commentsCount: Int?,
    val id: String?,
    val likesCount: Int?,
    val like: Boolean?,
    val media: ArrayList<MediaData>?,
    val name: String?,
    val postDate: String?,
    val postsCount: Int?,
    val profilePicture: String?,
    val tag: ArrayList<String>?,
    val text: String?,
    val userId: String?,
    val username: String?,
    val views: Int?
)