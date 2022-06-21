package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Microscopist
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.ui.login.LoginPresenter
import net.aiscope.gdd_app.ui.login.LoginView
import org.junit.Test

class LoginPresenterTest {

    companion object {
        val microscopistThatAgreedPrivacyPolicy = Microscopist("1", true, hasSubmitSampleFirstTime = false)
        val microscopistThatHasntAgreedPrivacyPolicy = Microscopist("1", false, hasSubmitSampleFirstTime = false)
    }

    private val viewMock: LoginView = mock()
    private val firebaseAuthenticatorMock: FirebaseAuthenticator = mock {
        on { isUserSignedIn() } doReturn true
    }
    private val microscopistRepository: MicroscopistRepository = mock()

    private val presenter = LoginPresenter(viewMock, mock(),
        firebaseAuthenticatorMock, microscopistRepository)

    @Test
    fun `goes to main when microscopist has agreed the privacy policy`() {
        whenever(microscopistRepository.load()) doReturn microscopistThatAgreedPrivacyPolicy

        presenter.start(mock())

        verify(viewMock).goToMain()
    }

    @Test
    fun `goes to the privacy policy when the microscopist hasn't agreed it`() {
        whenever(microscopistRepository.load()) doReturn microscopistThatHasntAgreedPrivacyPolicy

        presenter.start(mock())

        verify(viewMock).goToPrivacyPolicy()
    }
}
