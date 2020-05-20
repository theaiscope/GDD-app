package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.model.MicroscopeQuality

data class MicroscopeQualityDto(
    @SerializedName("isDamaged") val isDamaged: Boolean,
    @SerializedName("magnification") val magnification: Int
) {
    fun toDomain() = MicroscopeQuality(
        isDamaged,
        magnification
    )
}
