package net.aiscope.gdd_app.network

import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample

class FirebaseRemoteStorage(private val uploader: FirebaseStorageUploader, private val gson: Gson) : RemoteStorage {

    override suspend fun upload(sample: Sample) {
        val jsonKey = "${sample.id}/metadata.json"

        uploader.upload(gson.toJson(sample.toDto()), jsonKey)

        sample.images.forEachIndexed { index, image ->
            uploader.upload(image, "${sample.id}/image_${index}.png")
        }

        sample.masks.forEachIndexed { index, mask ->
            uploader.upload(mask, "${sample.id}/mask_${index}.png")
        }
    }
}
