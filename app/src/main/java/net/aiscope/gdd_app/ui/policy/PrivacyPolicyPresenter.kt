package net.aiscope.gdd_app.ui.policy

import javax.inject.Inject

class PrivacyPolicyPresenter @Inject constructor(
    private val view: PrivacyPolicyView
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
        TODO("Not yet implemented")
    }

    fun onRetryClicked() {
        view.showLoading()
        view.loadPolicy()
    }
}
