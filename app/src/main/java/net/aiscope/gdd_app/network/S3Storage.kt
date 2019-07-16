package net.aiscope.gdd_app.network

import net.aiscope.gdd_app.model.Sample
import java.io.File

class S3Storage(val uploader: S3Uploader) : RemoteStorage {
    override fun upload(sample: Sample) {
        val jsonKey = "${sample.id}/metadata.json"
        val imageKey = "${sample.id}/image.jpg"
        val maskKey = "${sample.id}/mask.jpg"

        uploader.upload(sample.toJson(), sample.id, jsonKey)

        sample.imagePath?.let {
            uploader.upload(File(it), imageKey)
        }

        sample.maskPath?.let {
            uploader.upload(File(it), maskKey)
        }
    }
}