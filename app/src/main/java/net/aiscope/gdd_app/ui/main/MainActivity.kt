package net.aiscope.gdd_app.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.dropdown_select_disease
import kotlinx.android.synthetic.main.activity_main.main_continue_button
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.login.LoginActivity
import net.aiscope.gdd_app.ui.sample_preparation.SamplePreparationActivity
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import javax.inject.Inject

class MainActivity : AppCompatActivity(), SelectDiseaseView, LogoutFLow {

    @Inject
    lateinit var presenter: SelectDiseasePresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        askCameraPermission()

        main_continue_button.setOnClickListener {
            coroutineScope.launch {
                presenter.saveDisease(dropdown_select_disease.selectedItem.toString())
            }
        }

        if(intent.hasExtra("SAMPLE_SAVED")) {
            CustomSnackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.metadata_snackbar_success),
                Snackbar.LENGTH_LONG,
                R.drawable.ic_thumb_up)
                .show()
        }
    }

    private fun askCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(NoOpPermissionListener)
            .onSameThread()
            .check()
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun goToSamplePreparation() {
        val intent = Intent(this, SamplePreparationActivity::class.java)
        this.startActivity(intent)
    }

    override fun logout(success: Boolean) = if (success) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    } else {
        Toast.makeText(this, R.string.error_message_logout_failure, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_user -> {
                val itemView: View = this.findViewById(R.id.action_user)
                showLogoutDialog(itemView)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun logoutAction() = this.presenter.logout()
}
