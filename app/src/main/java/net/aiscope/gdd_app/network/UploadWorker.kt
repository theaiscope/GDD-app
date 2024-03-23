package net.aiscope.gdd_app.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber

class UploadWorker constructor(
    context: Context,
    params: WorkerParameters,
    private val repo: SampleRepository,
    private val storage: RemoteStorage
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        when (val id = inputData.getString("sample_id")) {
            null -> Result.failure()
            else -> {
                try {
                    val sample = repo.load(id)

                    storage.upload(sample)
                    Result.success()
                } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
                    Timber.e(error, "An error occurred when doing work")
                    Result.retry()
                }
            }
        }
    }
}
