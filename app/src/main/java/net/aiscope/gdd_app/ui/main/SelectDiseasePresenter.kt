package net.aiscope.gdd_app.ui.main

import com.google.firebase.auth.FirebaseAuth
import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.repository.SampleRepository
import javax.inject.Inject

class SelectDiseasePresenter @Inject constructor(
    val view: SelectDiseaseView,
    val firebaseAuth: FirebaseAuthenticator,
    val repository: SampleRepository
) {

    private val logoutCallBack: FirebaseAuthenticator.LogoutCallBack = object : FirebaseAuthenticator.LogoutCallBack {
        override fun onSignOnSuccess() {
            view.loginSuccess()
        }

        override fun onSignOutFailure() {
            view.loginFailure()
        }
    }

    suspend fun saveDisease(input: String) {
        if (!input.isBlank()) {
            val sample = repository.create().copy(disease = input)
            repository.store(sample)

            view.captureImage(sample.nextImageName(), sample.nextMaskName())
            view.showSuccessToast()
        } else {
            view.showFailureToast()
        }
    }

    fun logout() {
        firebaseAuth.singOut(logoutCallBack)
    }
}
