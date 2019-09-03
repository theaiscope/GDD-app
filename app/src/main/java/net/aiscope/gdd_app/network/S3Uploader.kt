package net.aiscope.gdd_app.network

import android.content.Context
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import net.aiscope.gdd_app.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class S3Uploader(val context: Context) {

    val s3: AmazonS3 = AmazonS3Client(S3Credentials(), Region.getRegion(Regions.US_EAST_1))

    private val transfer: TransferUtility = TransferUtility.builder().s3Client(s3).context(context).build()

    suspend fun upload(file: File, key: String) {
        return suspendCoroutine {cont ->
            var continuationResumed = false
            val observer = transfer.upload(BuildConfig.S3_BUCKET, key, file)

            observer.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Timber.tag("S3Uploader")
                        .i("onProgressChanged key $key progres: ${bytesCurrent}/${bytesTotal}")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    Timber.tag("S3Uploader").i("stateChanged key $key state $state")

                    if (state == TransferState.COMPLETED) {
                        continuationResumed = true
                        cont.resume(Unit)
                    } else if(state == TransferState.FAILED || state == TransferState.CANCELED) {
                        Timber.tag("S3Uploader").w("failed to upload $state")
                        if (!continuationResumed) cont.resumeWithException(Exception("failed to upload $state"))
                    }
                }

                override fun onError(id: Int, ex: Exception?) {
                    Timber.tag("S3Uploader").e(ex, "error key $key ex: ${ex?.toString()}")
                    continuationResumed = true
                    cont.resumeWithException(Exception("failed to upload $id", ex))
                }
            })
        }
    }

    suspend fun upload(data: String, id: String, key: String) {
        val file = File(context.cacheDir, "${id}.json")

        // code copied from https://stackoverflow.com/questions/35481924/write-a-string-to-a-file/35481977 (not the best code in the world)
        try {
            file.createNewFile()
            val fOut = FileOutputStream(file)
            val myOutWriter = OutputStreamWriter(fOut)
            myOutWriter.append(data)

            myOutWriter.close()

            fOut.flush()
            fOut.close()

            return upload(file, key)
        } catch (e: IOException) {
            Timber.e(e, "File write failed: $e")
        }
    }

    private fun S3Credentials() : AWSCredentials {
        return object : AWSCredentials {
            override fun getAWSAccessKeyId(): String {
                return BuildConfig.AWS_ACCESS
            }

            override fun getAWSSecretKey(): String {
                return BuildConfig.AWS_SECRET
            }
        }
    }
}
