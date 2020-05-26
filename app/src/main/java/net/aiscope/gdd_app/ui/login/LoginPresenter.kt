package net.aiscope.gdd_app.ui.login

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.repository.HealthFacilityRepository
import net.aiscope.gdd_app.repository.MicroscopistRepository
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val firebaseAuthenticator: FirebaseAuthenticator,
    private val microscopistRepository: MicroscopistRepository
) {

    private val callback: FirebaseAuthenticator.Callback = object : FirebaseAuthenticator.Callback {
        override fun onSignInSuccess() {
            healthFacilityRepository.cacheHealthFacility()
            goIn()
        }

        override fun onSignInFailure() {
            view.exit()
        }
    }

    private fun goIn() {
        if (microscopistRepository.load().hasAcceptedPrivacyPolicy) {
            view.goToMain()
        } else {
            view.goToPrivacyPolicy()
        }
    }

    fun start(logIn: ActivityResultLauncher<Intent>) {
        if (firebaseAuthenticator.isUserSignedIn()) {
            healthFacilityRepository.cacheHealthFacility()
            goIn()
        } else {
            firebaseAuthenticator.startFirebaseAuthSignIn(logIn, callback)
        }
    }

    fun checkActivityResult(resultCode: Int, data: Intent?) {
        firebaseAuthenticator.checkForSignInResult(resultCode, data)
    }
}
