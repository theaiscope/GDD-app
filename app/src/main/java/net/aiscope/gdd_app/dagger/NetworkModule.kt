package net.aiscope.gdd_app.dagger

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.network.FirebaseRemoteStorage
import net.aiscope.gdd_app.network.FirebaseStorageUploader
import net.aiscope.gdd_app.network.RemoteStorage

@Module
object NetworkModule {

    @Provides
    fun gson(): Gson = Gson()

    @Provides
    fun remoteStorage(firebaseStorageUploader: FirebaseStorageUploader, gson: Gson): RemoteStorage =
        FirebaseRemoteStorage(firebaseStorageUploader, gson)
}
