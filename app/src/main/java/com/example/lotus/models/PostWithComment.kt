package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

class PostWithComment (
    val id: String?,
    val username: String?,
    val profilePicture: String?,
    val name: String?,
    val likesCount: Int?,
    val views: String?,
    val date: String?,
    val text: String?,
    val liked: Int?,
    val postId: String?,
    val belongsTo: String?,
    val tag: ArrayList<String>?,
    val media: ArrayList<MediaData>?,
    val commentsCount: Int?,
    val comments: ArrayList<Comment>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        TODO("tag"),
        TODO("media"),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("comments")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(profilePicture)
        parcel.writeString(name)
        parcel.writeValue(likesCount)
        parcel.writeString(views)
        parcel.writeString(date)
        parcel.writeString(text)
        parcel.writeValue(liked)
        parcel.writeString(postId)
        parcel.writeString(belongsTo)
        parcel.writeValue(commentsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostWithComment> {
        override fun createFromParcel(parcel: Parcel): PostWithComment {
            return PostWithComment(parcel)
        }

        override fun newArray(size: Int): Array<PostWithComment?> {
            return arrayOfNulls(size)
        }
    }

}