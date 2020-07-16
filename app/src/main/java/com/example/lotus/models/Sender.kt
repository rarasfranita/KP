package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

class Sender(
    val id: String?,
    val username: String?,
    val name: String?,
    val profilePicture: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(name)
        parcel.writeString(profilePicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sender> {
        override fun createFromParcel(parcel: Parcel): Sender {
            return Sender(parcel)
        }

        override fun newArray(size: Int): Array<Sender?> {
            return arrayOfNulls(size)
        }
    }
}