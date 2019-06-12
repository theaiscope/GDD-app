package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.HealthFacility

interface HospitalRepository {
    fun store(healthFacility: HealthFacility)
    fun load(): String
    fun delete()
}