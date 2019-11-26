package net.aiscope.gdd_app.ui.mask

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_mask.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.writeToFile
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.mask.customview.MaskCustomView
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import java.io.File
import javax.inject.Inject

class MaskActivity : AppCompatActivity(), MaskView {

    companion object {
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_IMAGE_NAME"
        const val EXTRA_MASK_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_MASK_NAME"
    }

    @Inject
    lateinit var presenter: MaskPresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mask)
        setSupportActionBar(toolbar)
        attachCaptureFlowToolbar(toolbar)

        val imageNameExtra = intent.getStringExtra(EXTRA_IMAGE_NAME)
        val maskNameExtra = intent.getStringExtra(EXTRA_MASK_NAME)

        presenter.start(imageNameExtra)

        getBitmap.setOnClickListener { presenter.handleCaptureBitmap(maskNameExtra) }
        erase.setOnClickListener { presenter.eraseMode() }
        brush.setOnClickListener { presenter.brushMode() }
        move.setOnClickListener { presenter.moveMode() }
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun takeMask(maskName: String, onPhotoReceived: (File?) -> Unit) {
        val bmp = maskView.maskBitmap
        if (bmp == null) {
            onPhotoReceived(null)
        } else {
            val dest = File(this.filesDir, "${maskName}.jpg")
            bmp.writeToFile(dest)

            onPhotoReceived(dest)
        }
    }

    override fun goToMetadata() {
        val intent = Intent(this, MetadataActivity::class.java)
        startActivity(intent)
    }

    override fun notifyImageCouldNotBeTaken() {
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT).show()
    }

    override fun loadBitmap(imagePath: String) {
        coroutineScope.launch {
            val bmp = readImage(imagePath)
            maskView.originalBitmap = bmp
        }
    }

    override fun brushMode() {
        maskView.mode = MaskCustomView.DrawMode.Brush
    }

    override fun eraseMode() {
        maskView.mode = MaskCustomView.DrawMode.Erase
    }

    override fun moveMode() {
        maskView.mode = MaskCustomView.DrawMode.Move
    }

    private suspend fun readImage(filepath: String): Bitmap = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeFile(filepath, options)
    }
}
