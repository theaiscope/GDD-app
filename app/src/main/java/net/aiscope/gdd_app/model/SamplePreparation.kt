package net.aiscope.gdd_app.model

data class SamplePreparation(
    val waterType: WaterType,
    val usesGiemsa: Boolean,
    val giemsaFP: Boolean,
    val usesPbs: Boolean,
    val usesAlcohol: Boolean,
    val reusesSlides: Boolean
)
