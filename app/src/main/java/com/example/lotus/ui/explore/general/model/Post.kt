package com.example.lotus.ui.explore.general.model

import android.os.Parcel
import android.os.Parcelable
import com.example.lotus.models.MediaData

data class Post(
    val bio: String?,
    val commentsCount: Int?,
    val id: String?,
    val likesCount: Int?,
    val media: ArrayList<MediaData>,
    val name: String?,
    val postDate: String?,
    val postsCount: Int?,
    val profilePicture: String?,
    val tag: ArrayList<String>,
    val text: String?,
    val like: Boolean?,
    val userId: String?,
    val username: String?,
    val views: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        TODO("media"),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        TODO("tag"),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bio)
        commentsCount?.let { parcel.writeInt(it) }
        parcel.writeString(id)
        likesCount?.let { parcel.writeInt(it) }
        parcel.writeString(name)
        parcel.writeString(postDate)
        postsCount?.let { parcel.writeInt(it) }
        parcel.writeString(profilePicture)
        parcel.writeString(text)
        parcel.writeValue(like)
        parcel.writeString(userId)
        parcel.writeString(username)
        views?.let { parcel.writeInt(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}