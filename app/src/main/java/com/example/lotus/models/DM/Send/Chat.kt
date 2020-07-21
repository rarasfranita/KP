package com.example.lotus.models.DM.Send

import android.os.Parcel
import android.os.Parcelable
import com.example.lotus.models.DM.Channel.ReceiverX

data class Chat(val channelId: String?, val createdAt: String?, val isRead: Boolean?, val `receiver`: ReceiverX?, val sender: Sender?, val text: String?, val type: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        TODO("receiver"),
        TODO("sender"),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(channelId)
        parcel.writeString(createdAt)
        parcel.writeByte(if (this!!.isRead!!) 1 else 0)
        parcel.writeString(text)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}