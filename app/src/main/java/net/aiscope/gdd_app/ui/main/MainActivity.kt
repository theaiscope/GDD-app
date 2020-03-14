package net.aiscope.gdd_app.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity(), SelectDiseaseView {

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

        button_go_to_capture.setOnClickListener {
            coroutineScope.launch {
                presenter.saveDisease(dropdown_select_disease.selectedItem.toString())
            }
        }
    }

    private fun askCameraPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(NoOpPermissionListener)
            .onSameThread()
            .check()
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun captureImage(nextImageName: String, nextMaskName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        intent.putExtra(CaptureImageActivity.EXTRA_MASK_NAME, nextMaskName)
        this.startActivity(intent)
    }

    override fun showSuccessToast() {
        Toast.makeText(this, R.string.confirmation_message_saved, Toast.LENGTH_SHORT).show()
    }

    override fun showFailureToast() {
        Toast.makeText(this, R.string.error_message_field_empty, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
