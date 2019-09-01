package net.aiscope.gdd_app.repository

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample
import javax.inject.Inject

class SampleRepositorySharedPreference @Inject constructor(
    private val store: SharedPreferenceStore,
    private val uuid: UUID,
    private val hospitalRepository: HospitalRepository
) : SampleRepository {

    private var currentSample: Sample? = null

    override fun current(): Sample {
        return currentSample ?: create()
    }

    override fun create(): Sample {
        val uuid = uuid.generateUUID()
        val facility = hospitalRepository.load()
        val sample = Sample(uuid, facility)

        currentSample = sample
        return sample
    }

    override fun store(sample: Sample) {
        store.store(sample.id, sample.toJson())
        currentSample = sample
    }

    override fun load(id: String): Sample {
        val json = store.load(id);
        val sample: Sample = Gson().fromJson(json)
        currentSample = sample

        return sample
    }

    override fun all(): List<Sample> {
        val jsons = store.all()

        return jsons.map {
            Gson().fromJson<Sample>(it)
        }.toList()
    }
}
