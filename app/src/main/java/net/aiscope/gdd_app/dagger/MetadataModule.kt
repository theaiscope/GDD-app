package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import net.aiscope.gdd_app.ui.metadata.MetadataMapper
import net.aiscope.gdd_app.ui.metadata.MetadataView

@Module
abstract class MetadataModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: MetadataActivity): MetadataView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: MetadataActivity): Activity

    @Module
    companion object {
        @Provides
        @PerActivity
        @JvmStatic
        internal fun provideMetadataMapper(): MetadataMapper = MetadataMapper
    }
}
