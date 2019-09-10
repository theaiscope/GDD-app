package net.aiscope.gdd_app.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.network.S3Storage
import net.aiscope.gdd_app.network.S3Uploader

@Module
object NetworkModule {

    @Provides
    @JvmStatic
    fun gson(): Gson = Gson()

    @Provides
    @JvmStatic
    fun uploader(context: Context): S3Uploader = S3Uploader(context)

    @Provides
    @JvmStatic
    fun remoteStorage(s3uploader: S3Uploader, gson: Gson): RemoteStorage =
        S3Storage(s3uploader, gson)


}
