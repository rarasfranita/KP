package com.example.lotus.models

import android.os.Parcel
import android.os.Parcelable

class PostWithComment (
    val id: String?,
    val commentsCount: Int?,
    val comments: ArrayList<Comment>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("comments")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(commentsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostWithComment> {
        override fun createFromParcel(parcel: Parcel): PostWithComment {
            return PostWithComment(parcel)
        }

        override fun newArray(size: Int): Array<PostWithComment?> {
            return arrayOfNulls(size)
        }
    }
}