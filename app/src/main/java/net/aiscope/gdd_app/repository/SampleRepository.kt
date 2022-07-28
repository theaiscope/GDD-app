package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.Sample

interface SampleRepository {
    fun store(sample: Sample): Sample
    fun load(id: String): Sample
    suspend fun create(disease: String): Sample
    suspend fun current(): Sample

    fun all(): List<Sample>
    suspend fun lastSaved(): Sample?
}

class SampleRepositoryException(message: String): Exception(message)
