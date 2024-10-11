package com.example.divelog

import android.os.Parcel
import android.os.Parcelable

data class Dive(
    val location: String,
    val maxDepth: Float,
    val duration: Int,
    val date: String // New field for the dive date
) : Parcelable {

    // Constructor for Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString() ?: "" // Read the date from Parcel
    )

    // Writing data to Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location)
        parcel.writeFloat(maxDepth)
        parcel.writeInt(duration)
        parcel.writeString(date) // Write the date to Parcel
    }

    // Required override (for Parcelable)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable CREATOR to regenerate Dive object from Parcel
    companion object CREATOR : Parcelable.Creator<Dive> {
        override fun createFromParcel(parcel: Parcel): Dive {
            return Dive(parcel)
        }

        override fun newArray(size: Int): Array<Dive?> {
            return arrayOfNulls(size)
        }
    }
}
