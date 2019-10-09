package net.aiscope.gdd_app.ui

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.main.MainActivity

fun AppCompatActivity.attachCaptureFlowToolbar(toolbar: androidx.appcompat.widget.Toolbar) {

    fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    toolbar.setNavigationOnClickListener {
        with(AlertDialog.Builder(this, R.style.MyAlertDialogStyle)) {
            setPositiveButton(R.string.ok) { _, _ ->
                goToHome()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            setMessage(getString(R.string.alert_dialog_message))
            setTitle(getString(R.string.alert_dialog_title))
            create()
        }.show()
    }
}

