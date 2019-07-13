package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.metadata.MetadataActivity

@PerActivity
@Subcomponent(modules = [(MetadataModule::class)])
interface MetadataSubComponents : AndroidInjector<MetadataActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MetadataActivity>()
}

