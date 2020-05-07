package net.aiscope.gdd_app.ui.mask

import android.os.Parcel
import android.os.Parcelable

data class BrushDiseaseStage(val id: Int, val name: String, val maskColor: Int): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun toString() = name

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(maskColor)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<BrushDiseaseStage> {
        override fun createFromParcel(parcel: Parcel): BrushDiseaseStage {
            return BrushDiseaseStage(parcel)
        }

        override fun newArray(size: Int): Array<BrushDiseaseStage?> {
            return arrayOfNulls(size)
        }
    }
}
