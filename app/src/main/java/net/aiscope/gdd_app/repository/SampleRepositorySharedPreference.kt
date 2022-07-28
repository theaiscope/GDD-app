package net.aiscope.gdd_app.repository

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleStatus
import java.util.Calendar
import javax.inject.Inject

class SampleRepositorySharedPreference @Inject constructor(
    private val store: SharedPreferenceStore,
    private val uuid: UUID,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val gson: Gson
) : SampleRepository {

    private var currentSample: Sample? = null

    override suspend fun current(): Sample {
        return currentSample ?: lastIncomplete()
        ?: throw SampleRepositoryException("Current sample could not be retrieved")
    }

    override suspend fun create(disease: String): Sample {
        val uuid = uuid.generateUUID()
        val facility = healthFacilityRepository.load()
        val sample = Sample(
            uuid,
            facility.id,
            facility.microscopist,
            disease,
            createdOn = Calendar.getInstance(),
            lastModified = Calendar.getInstance()
        )

        currentSample = sample
        return sample
    }

    override fun store(sample: Sample): Sample {
        val updatedSample = sample.copy(lastModified = Calendar.getInstance())
        store.store(updatedSample.id, gson.toJson(updatedSample.toDto()))
        currentSample = updatedSample
        return updatedSample
    }

    override fun load(id: String): Sample {
        val json = store.load(id)
        val sample: Sample = gson.fromJson<SampleDto>(json).toDomain()
        currentSample = sample

        return sample
    }

    override fun all(): List<Sample> {
        val jsons = store.all().filter { it != "true" }

        return jsons.mapNotNull {
            kotlin.runCatching {
                gson.fromJson<SampleDto>(it).toDomain()
            }.getOrNull()
        }
    }

    override suspend fun lastSaved(): Sample? {
        val allStores = all()
        return allStores
            .filter { s -> s.status != SampleStatus.Incomplete }
            .maxByOrNull { it.createdOn }
    }

    private fun lastIncomplete(): Sample? {
        val allStores = all()
        return allStores
            .filter { s -> s.status == SampleStatus.Incomplete }
            .maxByOrNull { it.createdOn }
    }

}
