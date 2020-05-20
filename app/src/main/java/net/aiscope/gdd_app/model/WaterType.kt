package net.aiscope.gdd_app.model

@Suppress("MagicNumber") // these are IDs
enum class WaterType(val id: Int) {
    DISTILLED(1),
    BOTTLED(2),
    TAP(3),
    WELL(4)
}
