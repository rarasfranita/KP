package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val __v: Int?,
    val _id: String?,
    val avatar: String?,
    val bio: String?,
    val createdAt: String?,
    val deleted: Boolean?,
    val email: String?,
    val emailVerified: Boolean?,
    val name: String?,
    val password: String?,
    val phone: String?,
    val postsCount: Int?,
    val updatedAt: String?,
    val username: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (__v != null) {
            parcel.writeInt(__v)
        }
        parcel.writeString(_id)
        parcel.writeString(avatar)
        parcel.writeString(bio)
        parcel.writeString(createdAt)
        parcel.writeByte(if (this!!.deleted!!) 1 else 0)
        parcel.writeString(email)
        parcel.writeByte(if (this!!.emailVerified!!) 1 else 0)
        parcel.writeString(name)
        parcel.writeString(password)
        parcel.writeString(phone)
        if (postsCount != null) {
            parcel.writeInt(postsCount)
        }
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