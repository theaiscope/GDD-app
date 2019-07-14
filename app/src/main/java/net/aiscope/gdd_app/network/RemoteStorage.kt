package net.aiscope.gdd_app.network

import net.aiscope.gdd_app.model.Sample

interface RemoteStorage {
    fun upload(sample: Sample)
}