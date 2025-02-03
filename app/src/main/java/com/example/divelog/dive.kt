package com.example.divelog

import android.os.Parcel
import android.os.Parcelable

data class Dive(
    val location: String,
    val maxDepth: Float,
    val duration: Int,
    val date: String,
    val diveBuddy: String? = null, // Make this optional
    val weatherConditions: String? = null, // Make this optional
    val visibility: Float? = null, // Make this optional
    val waterTemperature: Float? = null, // Add this field for water temperature
    val isNightDive: Boolean = false // Make this optional
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(), // diveBuddy
        parcel.readString(), // weatherConditions
        parcel.readValue(Float::class.java.classLoader) as? Float, // visibility
        parcel.readValue(Float::class.java.classLoader) as? Float, // waterTemperature
        parcel.readInt() == 1 // isNightDive (converts int to boolean)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(location)
        parcel.writeFloat(maxDepth)
        parcel.writeInt(duration)
        parcel.writeString(date)
        parcel.writeString(diveBuddy) // Write diveBuddy
        parcel.writeString(weatherConditions) // Write weatherConditions
        parcel.writeValue(visibility) // Write visibility
        parcel.writeValue(waterTemperature) // Write waterTemperature
        parcel.writeInt(if (isNightDive) 1 else 0) // Write isNightDive
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
