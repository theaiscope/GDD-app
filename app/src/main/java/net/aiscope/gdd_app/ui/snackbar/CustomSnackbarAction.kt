package net.aiscope.gdd_app.ui.snackbar

import android.view.View

data class CustomSnackbarAction(
    val label: String,
    val listener: View.OnClickListener
)
