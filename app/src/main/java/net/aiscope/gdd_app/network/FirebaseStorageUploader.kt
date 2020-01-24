package net.aiscope.gdd_app.network

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseStorageUploader @Inject constructor() {

    suspend fun upload(file: File, key: String) {
        val fileUri = Uri.fromFile(file)
        val task = Firebase.storage.reference.child(key).putFile(fileUri)

        upload(task, key)
    }

    private suspend fun upload(task: UploadTask, key: String): Unit = suspendCoroutine { cont ->
        task.addOnFailureListener {
            Timber.tag("FirebaseStorageUploader").e(it, "error key $key ex: $it")
            cont.resumeWithException(it)
        }.addOnSuccessListener {
            Timber.tag("FirebaseStorageUploader")
                .i("success $key")
            cont.resume(Unit)
        }.addOnProgressListener {
            Timber.tag("FirebaseStorageUploader")
                .i("progress $key progres: ${it.bytesTransferred}/${it.totalByteCount}")
        }
    }

    suspend fun upload(data: String, key: String) {
        val metadata = storageMetadata {
            contentType = "application/json"
        }
        val task = Firebase.storage.reference.child(key).putBytes(data.toByteArray(), metadata)
        upload(task, key)
    }
}
