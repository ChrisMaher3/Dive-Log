package com.chris.divelog

import android.os.Parcel
import android.os.Parcelable

data class Certification(
    val id: Long = 0, // Default to 0 for new certifications
    val name: String,
    val organization: String,
    val year: String,
    val imageUri: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(organization)
        parcel.writeString(year)
        parcel.writeString(imageUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Certification> {
        override fun createFromParcel(parcel: Parcel): Certification {
            return Certification(parcel)
        }

        override fun newArray(size: Int): Array<Certification?> {
            return arrayOfNulls(size)
        }
    }
}
