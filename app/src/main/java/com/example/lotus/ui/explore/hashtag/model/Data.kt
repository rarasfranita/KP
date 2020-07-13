package com.example.lotus.ui.explore.hashtag.model

import android.os.Parcel
import android.os.Parcelable
import com.example.lotus.models.MediaData

data class Data(
    val bio: String?,
    val commentsCount: Int?,
    val id: String?,
    val liked: Int?,
    val like:Boolean?,
    val likesCount: Int?,
    val media: ArrayList<MediaData>,
    val name: String?,
    val postDate: String?,
    val postsCount: Int?,
    val profilePicture: String?,
    val tag: ArrayList<String>,
    val text: String?,
    val userId: String?,
    val username: String?,
    val views: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("media"),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        TODO("tag"),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}