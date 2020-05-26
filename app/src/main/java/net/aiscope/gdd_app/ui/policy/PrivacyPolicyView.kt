package net.aiscope.gdd_app.ui.policy

interface PrivacyPolicyView {
    fun loadPolicy()
    fun showLoading()
    fun showLoaded()
    fun showError(errorMessage: String?)
    fun goToMain()
}
