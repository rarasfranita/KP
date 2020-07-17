package com.example.lotus.ui.explore.general.model

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val counts: Int,
    val hashtag: String?,
    val posts: ArrayList<Post>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.createTypedArrayList(Post) as ArrayList<Post>
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(counts as Int)
        parcel?.writeString(hashtag)
    }

    override fun describeContents(): Int {
        return 0
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