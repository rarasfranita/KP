package com.example.lotus.models;

import android.os.Parcel
import android.os.Parcelable

public class Post(
    val id: Number,
    val username: String?,
    val profilePicture: String?,
    val name: String?,
    val likesCount: Int?,
    val commentsCount: Int?,
    val views: String?,
    val date: String?,
    val text: String?,
    val like: Boolean?,
    val tag: ArrayList<String>?,
    val media: ArrayList<MediaData>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("id"),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        TODO("tag"),
        TODO("media")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(profilePicture)
        parcel.writeString(name)
        parcel.writeInt(likesCount as Int)
        parcel.writeInt(commentsCount as Int)
        parcel.writeString(views)
        parcel.writeString(date)
        parcel.writeString(text)
        parcel.writeValue(like)
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
