package net.aiscope.gdd_app.ui.mask

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
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
    private val brushDiseaseStages by lazy { presenter.brushDiseaseStages }
    private lateinit var currentDiseaseStage: BrushDiseaseStage
    private val selectStagePopup by lazy { composeSelectStagePopup() }

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

        currentDiseaseStage =
            if (savedInstanceState == null) brushDiseaseStages[0]
            else savedInstanceState.getParcelable("currentDiseaseStage")!!
        refreshBrushDrawableColor()
        photo_mask_view.initBrushDiseaseStage(currentDiseaseStage)

        tools_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                draw_btn.id -> photo_mask_view.drawMode()
                zoom_btn.id -> photo_mask_view.zoomMode()
                erase_btn.id -> photo_mask_view.eraseMode()
            }
        }

        undo_btn.setOnClickListener {
            photo_mask_view.undo()
            setEnabled(undo_btn, photo_mask_view.undoAvailable())
            setEnabled(redo_btn, true)
        }

        redo_btn.setOnClickListener {
            photo_mask_view.redo()
            setEnabled(undo_btn, true)
            setEnabled(redo_btn, photo_mask_view.redoAvailable())
        }

        stages_btn.setOnClickListener { selectStagePopup.show() }

        get_bitmap_btn.setOnClickListener { presenter.handleCaptureBitmap(maskNameExtra) }

        photo_mask_view.onPathCompletedListener = View.OnTouchListener { _, _ ->
            setEnabled(undo_btn, photo_mask_view.undoAvailable())
            setEnabled(redo_btn, photo_mask_view.redoAvailable())
            false
        }

    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun takeMask(maskName: String, onPhotoReceived: suspend (File?) -> Unit) {
        val bmp = photo_mask_view.getMaskBitmap()
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
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT)
            .show()
    }

    override fun initPhotoMaskView(imagePath: String) {
        coroutineScope.launch {
            val bmp = readImage(imagePath)
            photo_mask_view.setImageBitmap(bmp)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("currentDiseaseStage", currentDiseaseStage)
        outState.putInt("undo_btn-visibility", undo_btn.visibility)
        outState.putInt("redo_btn-visibility", redo_btn.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentDiseaseStage = savedInstanceState.getParcelable("currentDiseaseStage")!!
        undo_btn.visibility = savedInstanceState.getInt("undo_btn-visibility", View.INVISIBLE)
        redo_btn.visibility = savedInstanceState.getInt("redo_btn-visibility", View.INVISIBLE)
    }

    private fun setDiseaseStage(brushDiseaseStage: BrushDiseaseStage) {
        currentDiseaseStage = brushDiseaseStage
        refreshBrushDrawableColor()
        photo_mask_view.setBrushDiseaseStage(brushDiseaseStage)
    }

    private fun composeSelectStagePopup(): SelectStagePopup {
        return SelectStagePopup(
            this,
            presenter.brushDiseaseStages,
            stages_btn,
            AdapterView.OnItemClickListener() { _, _, position, _ ->
                setDiseaseStage(presenter.brushDiseaseStages[position])
            }).apply {
            setDropDownGravity(Gravity.TOP)
        }
    }

    private fun refreshBrushDrawableColor() {
        val color = currentDiseaseStage.maskColor
        val stageBtnDrawable = draw_btn.compoundDrawables[0] as LayerDrawable
        stageBtnDrawable.getDrawable(0).apply {
            setTint(color)
        }
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
