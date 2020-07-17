package com.example.lotus.models;

import android.os.Parcel
import android.os.Parcelable

class Post(
    val id: String?,
    val username: String?,
    val profilePicture: String?,
    val name: String?,
    val likesCount: Int?,
    val commentsCount: Int?,
    val views: String?,
    val date: String?,
    val text: String?,
    val liked: Int?,
    val postId: String?,
    val belongsTo: String?,
    val tag: ArrayList<String>?,
    val media: ArrayList<MediaData>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createTypedArrayList(MediaData)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id as Int)
        parcel.writeString(username)
        parcel.writeString(profilePicture)
        parcel.writeString(name)
        parcel.writeInt(likesCount as Int)
        parcel.writeInt(commentsCount as Int)
        parcel.writeString(views)
        parcel.writeString(date)
        parcel.writeString(text)
//        parcel.writeInt(liked as Int)
        parcel.writeString(postId)
        parcel.writeString(belongsTo)
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
