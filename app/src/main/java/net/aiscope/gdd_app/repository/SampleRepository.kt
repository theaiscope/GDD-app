package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.Sample

interface SampleRepository {
    fun store(sample: Sample): Sample
    fun load(id: String): Sample
    suspend fun create(): Sample
    suspend fun current(): Sample

    fun all(): List<Sample>
    suspend fun last(): Sample?
}
