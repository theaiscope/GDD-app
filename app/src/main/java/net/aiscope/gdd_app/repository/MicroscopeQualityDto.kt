package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.MicroscopeQuality

data class MicroscopeQualityDto(
    val isDamaged: Boolean,
    val magnification: Int
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
