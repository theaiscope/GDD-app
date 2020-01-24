package net.aiscope.gdd_app.network

import com.google.gson.Gson
import net.aiscope.gdd_app.model.Sample

class S3Storage(private val uploader: S3Uploader, private val gson: Gson) : RemoteStorage {

    override suspend fun upload(sample: Sample) {
        val jsonKey = "${sample.id}/metadata.json"

        uploader.upload(gson.toJson(sample.toDto()), sample.id, jsonKey)

        sample.images.forEachIndexed { index, image ->
            uploader.upload(image, "${sample.id}/image_${index}.jpg")
        }

        sample.masks.forEachIndexed { index, mask ->
            uploader.upload(mask, "${sample.id}/mask_${index}.jpg")
        }
    }
}
