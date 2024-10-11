package com.example.divelog

import android.os.Parcel
import android.os.Parcelable

data class Dive(
    val location: String,
    val maxDepth: Float,
    val duration: Int,
    val date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location)
        parcel.writeFloat(maxDepth)
        parcel.writeInt(duration)
        parcel.writeString(date) // Make sure to include date
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dive> {
        override fun createFromParcel(parcel: Parcel): Dive {
            return Dive(parcel)
        }

        override fun newArray(size: Int): Array<Dive?> {
            return arrayOfNulls(size)
        }
    }
}
