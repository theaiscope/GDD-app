package net.aiscope.gdd_app.network

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

fun MicroscopeQuality.toDto() = MicroscopeQualityDto(
    isDamaged,
    magnification
)
