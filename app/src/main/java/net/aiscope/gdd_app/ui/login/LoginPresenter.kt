package net.aiscope.gdd_app.ui.login

import android.content.Intent
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val view: LoginView,
    private val firebaseAuthenticator: FirebaseAuthenticator
) {

    private val callback: FirebaseAuthenticator.Callback = object : FirebaseAuthenticator.Callback {
        override fun onSignInSuccess() {
            view.goIn()
        }

        override fun onSignInFailure() {
            view.exit()
        }
    }

    fun start() {
        if (firebaseAuthenticator.isUserSignedIn()) {
            view.goIn()
        } else {
            firebaseAuthenticator.startFirebaseAuthSignIn(callback)
        }
    }

    fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        firebaseAuthenticator.checkForSignInResult(requestCode, resultCode, data)
    }
}
