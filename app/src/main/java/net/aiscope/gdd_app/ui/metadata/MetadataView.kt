package net.aiscope.gdd_app.ui.metadata

interface MetadataView {
    fun fillForm(model: List<FieldModel>)
    fun showInvalidFormError()
    fun goToHome()

}
