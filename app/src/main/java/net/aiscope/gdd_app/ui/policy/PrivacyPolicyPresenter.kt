package net.aiscope.gdd_app.ui.policy

import net.aiscope.gdd_app.repository.MicroscopistRepository
import javax.inject.Inject

class PrivacyPolicyPresenter @Inject constructor(
    private val view: PrivacyPolicyView,
    private val microscopistRepository: MicroscopistRepository
) {

    fun start() {
        view.showLoading()
        view.loadPolicy()
    }

    fun onPolicyLoaded() {
        view.showLoaded()
    }

    fun onError(errorMessage: String?) {
        view.showError(errorMessage)
    }

    fun onPolicyAgreed() {
        saveAgreement()
        view.goToMain()
    }

    private fun saveAgreement() {
        microscopistRepository.store(
            microscopistRepository.load().copy(hasAcceptedPrivacyPolicy = true)
        )
    }

    fun onRetryClicked() {
        view.showLoading()
        view.loadPolicy()
    }
}
