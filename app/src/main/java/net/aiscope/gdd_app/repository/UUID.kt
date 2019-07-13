package net.aiscope.gdd_app.repository

object UUID {
    fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}