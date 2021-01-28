package net.aiscope.gdd_app.dagger

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Module
abstract class SampleCompletionModule {

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SampleCompletionActivity): Activity

    @Binds
    internal abstract fun bindViewModelFactory(factory: SampleCompletionViewModelFactory): ViewModelProvider.Factory

}

@Singleton
class SampleCompletionViewModelFactory @Inject constructor(
    private val repository: SampleRepository
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return modelClass.getConstructor(SampleRepository::class.java).newInstance(repository)
    }
}

