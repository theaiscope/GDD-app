package net.aiscope.gdd_app.ui.login

import android.content.Intent
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.repository.HealthFacilityRepository
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val firebaseAuthenticator: FirebaseAuthenticator
) {

    private val callback: FirebaseAuthenticator.Callback = object : FirebaseAuthenticator.Callback {
        override fun onSignInSuccess() {
            healthFacilityRepository.cacheHealthFacility()
            view.goIn()
        }

        override fun onSignInFailure() {
            view.exit()
        }
    }

    fun start() {
        if (firebaseAuthenticator.isUserSignedIn()) {
            healthFacilityRepository.cacheHealthFacility()
            view.goIn()
        } else {
            firebaseAuthenticator.startFirebaseAuthSignIn(callback)
        }
    }

    fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        firebaseAuthenticator.checkForSignInResult(requestCode, resultCode, data)
    }
}
