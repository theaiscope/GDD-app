package net.aiscope.gdd_app.dagger

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositoryFirestore
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
abstract class SampleCompletionModule {

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SampleCompletionActivity): Activity

    @Binds
    @PerActivity
    internal abstract fun bindViewModelFactory(factory: SampleCompletionViewModelFactory): ViewModelProvider.Factory

    companion object {
        @Provides
        @PerActivity
        internal fun provideViewModel(
            repository: SampleRepository,
            remoteStorage: RemoteStorage,
            context: Context,
            microscopistRepository: MicroscopistRepository,
            sampleRepositoryFirestore: SampleRepositoryFirestore
        ): SampleCompletionViewModel =
            SampleCompletionViewModel(
                repository, remoteStorage, context, microscopistRepository, sampleRepositoryFirestore
            )
    }
}

@Singleton
class SampleCompletionViewModelFactory @Inject constructor(
    private val provider: Provider<SampleCompletionViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == SampleCompletionViewModel::class.java) {
            return provider.get() as T
        }
        //Only SampleCompletionViewModel is supported
        throw IllegalArgumentException("SampleCompletionViewModelFactory can only produce SampleCompletionViewModel")
    }
}


