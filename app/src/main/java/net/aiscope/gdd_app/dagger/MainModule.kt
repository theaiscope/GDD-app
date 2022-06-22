package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.repository.SampleCollectionRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.main.MainActivity
import net.aiscope.gdd_app.ui.main.SelectDiseasePresenter
import net.aiscope.gdd_app.ui.main.SelectDiseaseView

@Module
abstract class MainModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: MainActivity): SelectDiseaseView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: MainActivity): Activity

    companion object {
        @Provides
        @PerActivity
        internal fun providePresenter(
            view: SelectDiseaseView,
            firebaseAuth: FirebaseAuthenticator,
            repository: SampleRepository,
            sampleCollectionRepository: SampleCollectionRepository

        ): SelectDiseasePresenter =
            SelectDiseasePresenter(
                view,
                firebaseAuth,
                repository,
                sampleCollectionRepository
            )
    }
}
