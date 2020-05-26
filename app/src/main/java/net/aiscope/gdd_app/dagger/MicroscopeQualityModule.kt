package net.aiscope.gdd_app.dagger

import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityActivity
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityMapper
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityView

@Module
abstract class MicroscopeQualityModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: MicroscopeQualityActivity): MicroscopeQualityView

    companion object {
        @Provides
        @PerActivity
        internal fun provideMapper(): MicroscopeQualityMapper = MicroscopeQualityMapper
    }
}
