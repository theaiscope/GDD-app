package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.HealthFacility

interface HealthFacilityRepository {
    suspend fun load(): HealthFacility
    fun cacheHealthFacility()
}
