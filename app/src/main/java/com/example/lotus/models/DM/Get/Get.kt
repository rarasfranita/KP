package com.example.lotus.models.DM.Get

import android.os.Parcel
import android.os.Parcelable
import com.example.lotus.models.DM.Channel.ReceiverX
import com.example.lotus.models.DM.Send.Sender

data class Get(
    val __v: Int?,
    val _id: String?,
    val channelId: String?,
    val createdAt: String?,
    val deleted: Boolean?,
    val isRead: Boolean?,
    val receiver: ReceiverX?,
    val sender: Sender,
    val text: String?,
    val updatedAt: String?,
    var mine:Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        TODO("receiver"),
        TODO("sender"),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(__v)
        parcel.writeString(_id)
        parcel.writeString(channelId)
        parcel.writeString(createdAt)
        parcel.writeValue(deleted)
        parcel.writeValue(isRead)
        parcel.writeString(text)
        parcel.writeString(updatedAt)
        parcel.writeValue(mine)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Get> {
        override fun createFromParcel(parcel: Parcel): Get {
            return Get(parcel)
        }

        override fun newArray(size: Int): Array<Get?> {
            return arrayOfNulls(size)
        }
    }
}
