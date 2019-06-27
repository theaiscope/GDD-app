package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.verify
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.HealthFacility
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityPresenter
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class NewHealthFacilityPresenterTest {

    @Mock
    lateinit var view: NewHealthFacilityView

    @Mock
    lateinit var repository: HospitalRepository

    @InjectMocks
    lateinit var subject: NewHealthFacilityPresenter

    @Test
    fun shouldShowSaveMessageWhenSavedHospitalCallsOk() {
        subject.saveHospital("test")
        verify(view).showToast(R.string.confirmation_message_health_facility_saved)
    }
}