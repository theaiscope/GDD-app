package net.aiscope.gdd_app.ui.main

import net.aiscope.gdd_app.network.FirebaseAuthenticator
import net.aiscope.gdd_app.network.FirestoreUtil
import net.aiscope.gdd_app.repository.SampleRepository
import javax.inject.Inject

class SelectDiseasePresenter @Inject constructor(
    val view: SelectDiseaseView,
    val firebaseAuth: FirebaseAuthenticator,
    val repository: SampleRepository
) {

    private val logoutCallBack: FirebaseAuthenticator.LogoutCallBack = object : FirebaseAuthenticator.LogoutCallBack {
        override fun onSignOnSuccess() {
            view.logout(true)
        }

        override fun onSignOutFailure() {
            view.logout(false)
        }
    }

    suspend fun saveDisease(diseaseName: String) {
        val sample = repository.create(diseaseName)
        repository.store(sample)

        val sampleCollection = sample.buildSampleCollection();
        FirestoreUtil.FirestoreUtil.firestore?.collection("samples")?.
        document(sampleCollection.location)?.set(sampleCollection)

        view.goToCaptureImage(sample.nextImageName())
    }

    fun logout() {
        firebaseAuth.signOut(logoutCallBack)
    }
}
