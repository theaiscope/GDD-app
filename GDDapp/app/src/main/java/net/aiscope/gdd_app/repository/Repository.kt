package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.model.HealthFacility

interface Repository {
    fun store(healthFacility: HealthFacility)
    fun store(disease: Disease)
    fun load(): String
    fun delete()
}