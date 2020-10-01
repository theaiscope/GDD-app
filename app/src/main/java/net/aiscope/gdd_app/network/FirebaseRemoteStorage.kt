package net.aiscope.gdd_app.network

import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample

class FirebaseRemoteStorage(private val uploader: FirebaseStorageUploader, private val gson: Gson) : RemoteStorage {

    override suspend fun upload(sample: Sample) {
        val jsonKey = "${sample.id}/metadata.json"

        uploader.upload(gson.toJson(sample.toDto()), jsonKey)

        sample.images.completedCaptures.forEachIndexed { index, capture ->
            uploader.upload(capture.image, "${sample.id}/image_${index}.jpg")
            uploader.upload(capture.mask, "${sample.id}/mask_${index}.png")
        }
    }
}
