package net.aiscope.gdd_app.ui

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.main.MainActivity


interface CaptureFlow

fun <T> T.attachCaptureFlowToolbar(toolbar: androidx.appcompat.widget.Toolbar)
        where T : AppCompatActivity, T : CaptureFlow {
    toolbar.setNavigationOnClickListener {
        with(AlertDialog.Builder(this, R.style.AppTheme_Dialog)) {
            setPositiveButton(R.string.capture_flow_exit_dialog_exit) { _, _ ->
                goToHome()
            }
            setNegativeButton(R.string.capture_flow_exit_dialog_stay) { _, _ ->
                // do nothing
            }
            setMessage(getString(R.string.capture_flow_exit_dialog_message))
            setTitle(getText(R.string.capture_flow_exit_dialog_title))
            create()
        }.show()
    }
}

fun <T> T.goToHomeAndConfirmSaved() where T : AppCompatActivity, T : CaptureFlow {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.putExtra("SAMPLE_SAVED", "OK" )
    startActivity(intent)
}

fun <T> T.goToHome() where T : AppCompatActivity, T : CaptureFlow {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
}
