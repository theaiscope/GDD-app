package net.aiscope.gdd_app.network

import android.content.Context
import android.util.Log
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import java.io.File
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import java.lang.Exception
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class S3Uploader(val context: Context, val credentials: Credentials) {

    val s3: AmazonS3 = AmazonS3Client(S3Credentials())

    val transfer = TransferUtility.builder().s3Client(s3).context(context).build()

    suspend fun upload(file: File, key: String): Unit {
        return suspendCoroutine {cont ->
            val observer = transfer.upload("aiscope-test", key, file)

            observer.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Log.i("S3Uploader", "onProgressChanged key ${key} progres: ${bytesCurrent}/${bytesTotal}")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    Log.i("S3Uploader", "stateChanged key ${key} state ${state}")

                    if (state == TransferState.COMPLETED) {
                        cont.resume(Unit)
                    } else if(state == TransferState.FAILED || state == TransferState.CANCELED) {
                        cont.resumeWithException(Exception("failed to upload ${state}"))
                    }
                }

                override fun onError(id: Int, ex: Exception?) {
                    Log.e("S3Uploader", "error key ${key} ex: ${ex?.toString()}")
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
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    private fun S3Credentials() : AWSCredentials {
        return object : AWSCredentials {
            override fun getAWSAccessKeyId(): String {
                return credentials.API_KEY
            }

            override fun getAWSSecretKey(): String {
                return credentials.API_SECRET
            }
        }
    }
}
