package net.aiscope.gdd_app.network

import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import net.aiscope.gdd_app.R
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthenticator @Inject constructor(
    private val activity: Activity,
    private val auth: FirebaseAuth
) {

    companion object {
        const val RC_SIGN_IN = 1234
    }

    private var callback: Callback? = null

    fun isUserSignedIn() = auth.currentUser != null

    fun startFirebaseAuthSignIn(callback: Callback) {
        this.callback = callback
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(false).build()
                    )
                )
                .setTheme(R.style.AppTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    fun checkForSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> checkSignInResult(resultCode, data)
        }
    }

    private fun checkSignInResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            callback?.onSignInSuccess()
        } else {
            val response = IdpResponse.fromResultIntent(data)
            val logTag = "checkSignInResult"
            when {
                response == null -> Timber.tag(logTag).i("user canceled")
                response.error?.errorCode == ErrorCodes.NO_NETWORK -> Timber.tag(logTag).i("no network")
                else -> Timber.tag(logTag).e("unknown error")
            }

            callback?.onSignInFailure()
        }
        callback = null
    }

    interface Callback {
        fun onSignInSuccess()
        fun onSignInFailure()
    }
}
