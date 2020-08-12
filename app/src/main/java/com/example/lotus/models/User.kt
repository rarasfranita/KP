package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val __v: Int?,
    val _id: String?,
    val avatar: String?,
    val bio: String?,
    val createdAt: Boolean?,
    val deleted: Boolean?,
    val email: String?,
    val emailVerified: Boolean?,
    val name: String?,
    val password: String?,
    val phone: String?,
    val postsCount: Int?,
    val updatedAt: String?,
    var username: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(__v)
        parcel.writeString(_id)
        parcel.writeString(avatar)
        parcel.writeString(bio)
        parcel.writeValue(createdAt)
        parcel.writeValue(deleted)
        parcel.writeString(email)
        parcel.writeValue(emailVerified)
        parcel.writeString(name)
        parcel.writeString(password)
        parcel.writeString(phone)
        parcel.writeValue(postsCount)
        parcel.writeString(updatedAt)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}