package net.aiscope.gdd_app.dagger

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import net.aiscope.gdd_app.application.GddApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, HospitalModule::class,
    BuildersModule::class, SelectDiseaseModule::class])

interface AppComponent {

    @Component.Builder
    interface Builder {
        // provide Application instance into DI
        @BindsInstance
        fun application(application: GddApplication): Builder

        fun build(): AppComponent
    }

    fun inject(application: GddApplication)

}