package net.aiscope.gdd_app.ui.mask

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityMaskBinding
import net.aiscope.gdd_app.extensions.writeToFile
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import net.aiscope.gdd_app.ui.showConfirmBackDialog
import net.aiscope.gdd_app.ui.util.BitmapReader
import net.aiscope.gdd_app.ui.util.BitmapReader.MAX_TEXTURE_SIZE
import net.aiscope.gdd_app.ui.util.MaximumSizeDownSampling
import java.io.File
import javax.inject.Inject

@Suppress("TooManyFunctions")
class MaskActivity : AppCompatActivity(), MaskView, CaptureFlow {

    companion object {
        const val EXTRA_DISEASE_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_DISEASE_NAME"
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_IMAGE_NAME"
        const val EXTRA_MASK_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_MASK_NAME"
        const val EXTRA_MASK_PATH = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_MASK_PATH"
    }

    @Inject
    lateinit var presenter: MaskPresenter

    private val brushDiseaseStages by lazy { presenter.brushDiseaseStages }
    private var currentBrushColor: Int = 0
    private val selectStagePopup by lazy { composeSelectStagePopup() }

    private lateinit var binding: ActivityMaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val diseaseName = checkNotNull(intent.getStringExtra(EXTRA_DISEASE_NAME))
        val imageNameExtra = checkNotNull(intent.getStringExtra(EXTRA_IMAGE_NAME))
        val maskNameExtra = checkNotNull(intent.getStringExtra(EXTRA_MASK_NAME))
        val maskPathExtra = intent.getStringExtra(EXTRA_MASK_PATH)

        binding = ActivityMaskBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            presenter.start(diseaseName, imageNameExtra, maskPathExtra)

            currentBrushColor = savedInstanceState?.getInt("currentBrushColor")
                ?: brushDiseaseStages[0].maskColor
            refreshBrushDrawableColor()
            photoMaskView.initBrushColor(currentBrushColor)

            toolsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    drawBtn.id -> photoMaskView.drawMode()
                    zoomBtn.id -> photoMaskView.zoomMode()
                    eraseBtn.id -> photoMaskView.eraseMode()
                }
            }

            undoBtn.setOnClickListener {
                photoMaskView.undo()
                setEnabled(undoBtn, photoMaskView.undoAvailable())
                setEnabled(redoBtn, true)
            }

            redoBtn.setOnClickListener {
                photoMaskView.redo()
                setEnabled(undoBtn, true)
                setEnabled(redoBtn, photoMaskView.redoAvailable())
            }

            stagesBtn.setOnClickListener { selectStagePopup.show() }

            getBitmapBtn.setOnClickListener {
                presenter.handleCaptureBitmap(maskNameExtra, isEmptyMaskBitmap())
            }

            photoMaskView.onMaskingActionFinishedListener = View.OnTouchListener { _, _ ->
                setEnabled(undoBtn, photoMaskView.undoAvailable())
                setEnabled(redoBtn, photoMaskView.redoAvailable())
                false
            }
        }

    }

    override fun takeMask(maskName: String, onPhotoReceived: suspend (File?) -> Unit) {
        binding.photoMaskView.stopDrawing()
        val bmp = binding.photoMaskView.getMaskBitmap()
        lifecycleScope.launch {
            val dest = File(this@MaskActivity.filesDir, "${maskName}.png")
            bmp.writeToFile(dest, Bitmap.CompressFormat.PNG)

            onPhotoReceived(dest)
        }
    }

    override fun goToSampleCompletion() {
        val intent = Intent(this, SampleCompletionActivity::class.java)
        startActivity(intent)
        //Finish the activity to avoid keeping all the bitmaps in memory
        finish()
    }

    override fun notifyImageCouldNotBeTaken() {
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT)
            .show()
    }

    override fun initPhotoMaskView(imagePath: String, maskPath: String?) {
        lifecycleScope.launch {
            val bmp = readImage(imagePath)
            binding.photoMaskView.setImageBitmap(bmp)

            maskPath?.let {
                val mask = readImage(maskPath, mutable = true)
                mask.let {  binding.photoMaskView.setMaskBitmap(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentBrushColor", currentBrushColor)
        outState.putInt("undo_btn-visibility", binding.undoBtn.visibility)
        outState.putInt("redo_btn-visibility", binding.redoBtn.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentBrushColor = savedInstanceState.getInt("currentBrushColor")
        binding.undoBtn.visibility = savedInstanceState.getInt("undo_btn-visibility", View.INVISIBLE)
        binding.redoBtn.visibility = savedInstanceState.getInt("redo_btn-visibility", View.INVISIBLE)
    }

    override fun onBackPressed() {
        showConfirmBackDialog()
    }

    private fun setBrushColor(color: Int) {
        currentBrushColor = color
        refreshBrushDrawableColor()
        binding.photoMaskView.setBrushColor(color)
    }

    private fun composeSelectStagePopup(): SelectStagePopup {
        return SelectStagePopup(
            this,
            presenter.brushDiseaseStages,
            binding.stagesBtn
        ) { _, _, position, _ ->
            setBrushColor(presenter.brushDiseaseStages[position].maskColor)
        }.apply {
            setDropDownGravity(Gravity.TOP)
        }
    }

    private fun refreshBrushDrawableColor() {
        //Retrieve the 'virus' icon and set to the currently selected colour
        val stageSelector = binding.stagesBtn.drawable
        DrawableCompat.setTint(stageSelector, currentBrushColor)
    }

    private fun setEnabled(view: View, enabled: Boolean) {
        view.isInvisible = !enabled
    }

    private suspend fun readImage(filepath: String, mutable: Boolean = false): Bitmap = withContext( Dispatchers.IO) {
        BitmapReader.decodeSampledBitmapFromResource(
            File(filepath),
            MaximumSizeDownSampling(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE),
            mutable = mutable
        )
    }

    private fun isEmptyMaskBitmap(): Boolean {
        val maskBitmap = binding.photoMaskView.getMaskBitmap()
        val emptyBitmap: Bitmap =
            Bitmap.createBitmap(maskBitmap.width, maskBitmap.height, maskBitmap.config)
        return maskBitmap.sameAs(emptyBitmap)
    }
}
