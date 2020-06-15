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
        return currentSample ?: create()
    }

    override suspend fun create(): Sample {
        val uuid = uuid.generateUUID()
        val facility = healthFacilityRepository.load()
        val sample = Sample(uuid, facility.id, facility.microscopist, createdOn = Calendar.getInstance(), lastModified = Calendar.getInstance())

        currentSample = sample
        return sample
    }

    override fun store(sample: Sample) {
        //Wait. The 'toDto' misses the lastmodified. That just means it gets set later on upload.
        //Which is not what we want really
        val newSample = sample.copy(lastModified = Calendar.getInstance())
        println(newSample)
        val newDto = newSample.toDto()
        println(newDto)
        println(sample.toDto())
        val json = gson.toJson(sample.toDto())
        println("regular json")
        println(json)

        val newJson = gson.toJson(newDto)

        println("new json")
        println(newJson)
        store.store(sample.id, gson.toJson(newSample.toDto()))
//        store.store(sample.id, gson.toJson(sample.toDto()))
        currentSample = sample.copy(lastModified = Calendar.getInstance())
    }

    override fun load(id: String): Sample {
        val json = store.load(id)
        val sample: Sample = gson.fromJson<SampleDto>(json).toDomain()
        currentSample = sample

        return sample
    }

    override fun all(): List<Sample> {
        val jsons = store.all().filter { it != "true" }

        return jsons.map {
            kotlin.runCatching {
                gson.fromJson<SampleDto>(it).toDomain()
            }.getOrNull()
        }.filterNotNull()
    }

    override suspend fun last(): Sample? {
        val allStores = all()
        return allStores
            .filter{s -> s.createdOn != null
                        && s.status != SampleStatus.Incomplete}
            .sortedBy { it.createdOn }.lastOrNull()
    }
}
