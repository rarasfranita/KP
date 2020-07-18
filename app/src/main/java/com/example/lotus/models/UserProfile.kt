package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

class UserProfile(
    val username: String?,
    val _id: String?,
    val profilePicture: String?,
    val email: String?,
    val name: String?,
    val bio: String?,
    val follower: Int?,
    val following: Int?,
    val posts: ArrayList<Post>?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(Post)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(_id)
        parcel.writeString(profilePicture)
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(bio)
        parcel.writeValue(follower)
        parcel.writeValue(following)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserProfile> {
        override fun createFromParcel(parcel: Parcel): UserProfile {
            return UserProfile(parcel)
        }

        override fun newArray(size: Int): Array<UserProfile?> {
            return arrayOfNulls(size)
        }
    }
}