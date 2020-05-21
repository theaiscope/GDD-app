package net.aiscope.gdd_app.ui.mask

import android.content.res.Resources
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.repository.SampleRepository

class MaskPresenter(
    val view: MaskView,
    val repository: SampleRepository,
    val resources: Resources
) {

    private lateinit var diseaseName: String

    val brushDiseaseStages: Array<BrushDiseaseStage> by lazy {
        composeBrushDiseaseStagesArray(
            diseaseName,
            resources
        )
    }

    fun handleCaptureBitmap(maskName: String) {
        view.takeMask(maskName) { file ->
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.current().addMask(file)
                repository.store(sample)

                view.goToMetadata()
            }
        }
    }

    fun start(diseaseName: String, imagePath: String) {
        this.diseaseName = diseaseName
        view.initPhotoMaskView(imagePath)
    }

    companion object {

        private fun composeBrushDiseaseStagesArray(
            diseaseName: String,
            resources: Resources
        ): Array<BrushDiseaseStage> {
            val (stagesNamesArrayId, stagesColorsArrayId) = getDiseaseStagesArraysIds(
                diseaseName,
                resources
            )
            return composeBrushDiseaseStagesArray(
                stagesNamesArrayId,
                stagesColorsArrayId,
                resources
            )
        }

        private fun getDiseaseStagesArraysIds(diseaseName: String, resources: Resources) =
            when (diseaseName) {
                resources.getString(R.string.malaria_name) ->
                    R.array.malaria_stages_names to R.array.malaria_stages_colors
                else ->
                    throw IllegalArgumentException("$diseaseName not implemented in " +
                            "getDiseaseStagesArraysIds(diseaseName: String)")
            }

        private fun composeBrushDiseaseStagesArray(
            namesArrayId: Int,
            colorsArrayId: Int,
            resources: Resources
        ): Array<BrushDiseaseStage> {
            val names = resources.obtainTypedArray(namesArrayId)
            val colors = resources.obtainTypedArray(colorsArrayId)
            require(names.length() == colors.length())

            val result = (0 until names.length())
                .map { i -> BrushDiseaseStage(i, names.getString(i)!!, colors.getColor(i, 0)) }
                .toTypedArray()

            names.recycle()
            colors.recycle()

            return result
        }
    }
}
