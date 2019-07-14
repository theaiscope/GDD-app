package net.aiscope.gdd_app.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.network.Credentials
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.network.S3Storage
import net.aiscope.gdd_app.network.S3Uploader
import net.aiscope.gdd_app.repository.*
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun remoteStorage(s3uploader: S3Uploader): RemoteStorage = S3Storage(s3uploader)

    @Provides
    fun uploader(context: Context):S3Uploader = S3Uploader(context, Credentials)

}