package net.aiscope.gdd_app.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.aiscope.gdd_app.repository.SampleRepository

class UploadWorker constructor(
        context: Context,
        params: WorkerParameters,
        val repo: SampleRepository,
        val storage: RemoteStorage) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getString("sample_id")

        return when(id) {
            null -> Result.failure()
            else -> {
                try {
                    val sample = repo.load(id)

                    storage.upload(sample)
                    Result.success()
                } catch (error: Throwable) {
                    Result.retry()
                }
            }
        }
    }

}


class AppWorkerFactory(val repo: SampleRepository, val storage: RemoteStorage): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        // Since we have only one worker type now, if not we should use workerClassName to decide which Worker to create
        return UploadWorker(appContext, workerParameters, repo, storage)
    }
}