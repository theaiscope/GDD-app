package net.aiscope.gdd_app.dagger

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
abstract class SampleCompletionModule {

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SampleCompletionActivity): Activity

    @Binds
    internal abstract fun bindViewModelFactory(factory: SampleCompletionViewModelFactory): ViewModelProvider.Factory

    companion object {
        @Provides
        @PerActivity
        internal fun provideViewModel(repository: SampleRepository): SampleCompletionViewModel =
            SampleCompletionViewModel(repository)
    }
}

//@Singleton
//class SampleCompletionViewModelFactory @Inject constructor(
//    private val repository: SampleRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        Timber.i("We are calling the NEW create method here at least??")
////        return modelClass.getConstructor(SampleRepository::class.java).newInstance(repository)
//        if (modelClass == SampleCompletionViewModel::class.java) {
//            return SampleCompletionViewModel(repository) as T
//        }
//        //Only SampleCompletionViewModel is supported
//        throw IllegalArgumentException("SampleCompletionViewModelFactory can only produce SampleCompletionViewModel")
//    }
//}

@Singleton
class SampleCompletionViewModelFactory @Inject constructor(
    private val provider: Provider<SampleCompletionViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Timber.i("We are calling the PROVIDER create method here at least??")
        if (modelClass == SampleCompletionViewModel::class.java) {
            return provider.get() as T
        }
        //Only SampleCompletionViewModel is supported
        throw IllegalArgumentException("SampleCompletionViewModelFactory can only produce SampleCompletionViewModel")
    }
}


