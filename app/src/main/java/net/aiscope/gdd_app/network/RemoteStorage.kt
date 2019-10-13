package net.aiscope.gdd_app.network

import android.content.Context
import net.aiscope.gdd_app.model.Sample

interface RemoteStorage {
    suspend fun upload(sample: Sample)

    fun enqueue(sample: Sample, context: Context)
}
