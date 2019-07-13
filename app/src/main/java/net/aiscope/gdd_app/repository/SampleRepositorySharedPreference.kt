package net.aiscope.gdd_app.repository

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample
import javax.inject.Inject

class SampleRepositorySharedPreference @Inject constructor(val store: SharedPreferenceStore) : SampleRepository {

    override fun store(sample: Sample) {
        val json = Gson().toJson(sample)
        store.store(sample.id, json)
    }

    override fun load(id: String): Sample {
        val json = store.load(id);
        return Gson().fromJson(json)
    }

}