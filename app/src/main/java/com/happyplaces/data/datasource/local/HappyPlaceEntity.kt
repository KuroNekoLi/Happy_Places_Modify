package com.happyplaces.data.datasource.local

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_data_table")
data class HappyPlaceEntity(
    @PrimaryKey
    val id: Int,
    val title: String?,
    val image: Uri?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeParcelable(image, flags)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HappyPlaceEntity> {
        override fun createFromParcel(parcel: Parcel): HappyPlaceEntity {
            return HappyPlaceEntity(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlaceEntity?> {
            return arrayOfNulls(size)
        }
    }
}