package net.aiscope.gdd_app

import androidx.test.platform.app.InstrumentationRegistry
import it.cosenonjaviste.daggermock.DaggerMock
import net.aiscope.gdd_app.application.GddApplication
import net.aiscope.gdd_app.dagger.AppComponent
import net.aiscope.gdd_app.dagger.RepositoryModule

fun espressoDaggerMockRule() = DaggerMock.rule<AppComponent>(/*AppModule(app)*/ RepositoryModule()) {
    set { component -> component.inject(app) }
    customizeBuilder<AppComponent.Builder> { it.application(app) }
}

val app: GddApplication get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GddApplication
