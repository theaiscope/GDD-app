package net.aiscope.gdd_app.application

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.network.UploadWorker
import net.aiscope.gdd_app.repository.SampleRepository

class AppWorkerFactory(private val repo: SampleRepository, private val storage: RemoteStorage): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        // Since we have only one worker type now, if not we should use workerClassName to decide which Worker to create
        return UploadWorker(appContext, workerParameters, repo, storage)
    }
}
