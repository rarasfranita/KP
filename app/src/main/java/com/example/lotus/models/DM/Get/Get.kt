package com.example.lotus.models.DM.Get

import android.os.Parcel
import android.os.Parcelable

data class Get(
    val __v: Int?,
    val _id: String?,
    val channelId: String?,
    val createdAt: String?,
    val deleted: Boolean?,
    val isRead: Boolean?,
    val `receiver`: Receiver?,
    val sender: Sender?,
    val text: String?,
    val updatedAt: String?,
    var mine:Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        TODO("receiver"),
        TODO("sender"),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (__v != null) {
            parcel.writeInt(__v)
        }
        parcel.writeString(_id)
        parcel.writeString(channelId)
        parcel.writeString(createdAt)
        parcel.writeByte(if (this.deleted!!) 1 else 0)
        parcel.writeByte(if (this.isRead!!) 1 else 0)
        parcel.writeString(text)
        parcel.writeString(updatedAt)
        if (mine != null) {
            parcel.writeInt(mine!!)
        }
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