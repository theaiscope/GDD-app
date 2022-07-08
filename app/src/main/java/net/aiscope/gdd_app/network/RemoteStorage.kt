package net.aiscope.gdd_app.network

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import net.aiscope.gdd_app.model.Sample

interface RemoteStorage {
    suspend fun upload(sample: Sample)

    fun enqueue(sample: Sample, context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val imageData = workDataOf("sample_id" to sample.id)

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .addTag("upload")
            .setInputData(imageData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}
