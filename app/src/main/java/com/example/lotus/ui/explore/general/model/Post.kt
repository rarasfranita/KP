package com.example.lotus.ui.explore.general.model

import android.os.Parcel
import android.os.Parcelable
import com.example.lotus.models.MediaData

data class Post(
    val id: String?,
    val userId: String?,
    val profilePicture: String?,
    val username: String?,
    val bio: String?,
    val postsCount: Int?,
    val likesCount: Int?,
    val liked: Int?,
    val commentsCount: Int?,
    val postDate: String?,
    val media: ArrayList<MediaData>?,
    val tag: ArrayList<String>?,
    val views: Int?,
    val text: String?,
    val name: String?,
    val belongsTo: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.createTypedArrayList(MediaData),
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(profilePicture)
        parcel.writeString(username)
        parcel.writeString(bio)
        parcel.writeValue(postsCount)
        parcel.writeValue(likesCount)
        parcel.writeValue(liked)
        parcel.writeValue(commentsCount)
        parcel.writeString(postDate)
        parcel.writeValue(views)
        parcel.writeString(text)
        parcel.writeString(name)
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