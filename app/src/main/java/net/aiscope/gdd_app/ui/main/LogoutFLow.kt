package net.aiscope.gdd_app.ui.main

import android.view.ContextThemeWrapper
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.CaptureFlow

interface LogoutFLow {
    fun logoutAction()
}

fun <T> T. showLogoutDialog(itemView: View) where T : AppCompatActivity, T : LogoutFLow {
    val context = ContextThemeWrapper(this, R.style.PopupLogoutMenu)
    val popup = PopupMenu(context, itemView)
    val inflater: MenuInflater = popup.menuInflater
    inflater.inflate(R.menu.menu_options, popup.menu)
    popup.setOnMenuItemClickListener {
        when(it.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> false
        }
    }
    popup.show()
}

fun <T> T.showLogoutDialog() where T : AppCompatActivity, T : LogoutFLow {
    with(AlertDialog.Builder(this, R.style.AppTheme_Dialog)) {
        setPositiveButton(R.string.capture_flow_exit_dialog_exit) { _, _ ->
            logoutAction()
        }
        setNegativeButton(R.string.capture_flow_exit_dialog_stay) { _, _ ->
            // do nothing
        }
        setMessage(getString(R.string.logout_flow_exit_dialog_message))
        create()
    }.show()
}

