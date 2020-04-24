package net.aiscope.gdd_app.ui.mask

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
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
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import java.io.File
import javax.inject.Inject

@Suppress("TooManyFunctions")
class MaskActivity : AppCompatActivity(), MaskView, CaptureFlow {

    companion object {
        const val EXTRA_DISEASE_NAME = "net.aiscope.gdd_app.ui.mask.MaskActivity.EXTRA_DISEASE_NAME"
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

        val diseaseName = checkNotNull(intent.getStringExtra(EXTRA_DISEASE_NAME))
        val imageNameExtra = checkNotNull(intent.getStringExtra(EXTRA_IMAGE_NAME))
        val maskNameExtra = checkNotNull(intent.getStringExtra(EXTRA_MASK_NAME))

        presenter.start(diseaseName, imageNameExtra)

        if (savedInstanceState == null) {
            presenter.initBrushDiseaseStage()
        }

        tools_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                draw_btn.id -> presenter.drawMode()
                zoom_btn.id -> presenter.zoomMode()
            }
        }

        undo_btn.setOnClickListener { presenter.drawUndo() }

        redo_btn.setOnClickListener { presenter.drawRedo() }

        stages_btn.setOnClickListener { presenter.showSelectStageDialog(supportFragmentManager) }

        get_bitmap_btn.setOnClickListener { presenter.handleCaptureBitmap(maskNameExtra) }

        mask_custom_view.setOnTouchListener { _, _ ->
            refreshButtonsVisibility()
            false
        }
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun takeMask(maskName: String, onPhotoReceived: suspend (File?) -> Unit) {
        val bmp = mask_custom_view.getMaskBitmap()
        coroutineScope.launch {
            val dest = File(this@MaskActivity.filesDir, "${maskName}.jpg")
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
            mask_custom_view.setImageBitmap(bmp)
        }
    }

    override fun zoomMode() = mask_custom_view.zoomMode()

    override fun drawMode() = mask_custom_view.drawMode()

    override fun drawUndo() {
        mask_custom_view.undo()
        setEnabled(undo_btn, mask_custom_view.undoAvailable())
        setEnabled(redo_btn, true)
    }

    override fun drawRedo() {
        mask_custom_view.redo()
        setEnabled(undo_btn, true)
        setEnabled(redo_btn, mask_custom_view.redoAvailable())
    }

    override fun getBrushDiseaseStage() = mask_custom_view.getBrushDiseaseStage()

    override fun setBrushDiseaseStage(brushDiseaseStage: BrushDiseaseStage) {
        refreshBrushDrawable(brushDiseaseStage.maskColor)
        mask_custom_view.setBrushDiseaseStage(brushDiseaseStage)
    }

    private fun refreshBrushDrawable(color: Int) {
        val brush = draw_btn.compoundDrawables[1] as LayerDrawable
        val brushHeadTinted = resources.getDrawable(R.drawable.ic_brush_head, null).apply {
            setTint(color)
        }
        brush.setDrawable(1, brushHeadTinted)
    }

    private fun composeColoredBrushDrawable(color: Int): Drawable {
        val brushHeadless = resources.getDrawable(R.drawable.ic_brush_handle, null)
        val brushHead = resources.getDrawable(R.drawable.ic_brush_head, null).apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        return LayerDrawable(arrayOf(brushHeadless, brushHead))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("draw_btn-color", presenter.getBrushDiseaseStage().maskColor)
        outState.putInt("undo_btn-visibility", undo_btn.visibility)
        outState.putInt("redo_btn-visibility", redo_btn.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        refreshBrushDrawable(savedInstanceState.getInt("draw_btn-color"))
        undo_btn.visibility = savedInstanceState.getInt("undo_btn-visibility", View.INVISIBLE)
        redo_btn.visibility = savedInstanceState.getInt("redo_btn-visibility", View.INVISIBLE)
    }

    private fun refreshButtonsVisibility() {
        setEnabled(undo_btn, mask_custom_view.undoAvailable())
        setEnabled(redo_btn, mask_custom_view.redoAvailable())
    }

    private fun setEnabled(view: View, enabled: Boolean) {
        view.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
    }

    private suspend fun readImage(filepath: String): Bitmap = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeFile(filepath, options)
    }
}
