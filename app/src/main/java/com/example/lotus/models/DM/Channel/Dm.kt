package com.example.lotus.models.DM.Channel

import android.os.Parcel
import android.os.Parcelable

data class Dm(
    val _id: String?,
    val lastMessage: LastMessageX?,
    val name: String?,
    val participants: ArrayList<String>?,
    val participantsCount: Int?,
    val `receiver`: ReceiverX?,
    val type: String?,
    val isRead: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        TODO("lastMessage"),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readInt(),
        TODO("receiver"),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(name)
        parcel.writeStringList(participants)
        if (participantsCount != null) {
            parcel.writeInt(participantsCount)
        }
        parcel.writeString(type)
        if (isRead != null) {
            parcel.writeInt(isRead)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dm> {
        override fun createFromParcel(parcel: Parcel): Dm {
            return Dm(parcel)
        }

        override fun newArray(size: Int): Array<Dm?> {
            return arrayOfNulls(size)
        }
    }
}