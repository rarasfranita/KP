package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

class Notification(
    val type: String?,
    val postId: String?,
    val likesCount: Int?,
    val media: ArrayList<MediaData>?,
    val createdAt: String?,
    val target: String?,
    val isFollowing: Int?,
    val follower: Sender?,
    val sender: Sender?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("media"),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(Sender::class.java.classLoader),
        parcel.readParcelable(Sender::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(postId)
        parcel.writeValue(likesCount)
        parcel.writeString(createdAt)
        parcel.writeString(target)
        parcel.writeValue(isFollowing)
        parcel.writeParcelable(follower, flags)
        parcel.writeParcelable(sender, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}