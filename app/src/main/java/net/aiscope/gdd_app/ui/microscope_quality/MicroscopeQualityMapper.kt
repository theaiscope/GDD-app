package net.aiscope.gdd_app.ui.microscope_quality

import net.aiscope.gdd_app.model.MicroscopeQuality

object MicroscopeQualityMapper {

    fun convert(model: MicroscopeQuality?) =
        if (model == null)
            null
        else
            MicroscopeQualityViewStateModel(
                model.isDamaged,
                model.magnification
            )

    fun convert(viewModel: MicroscopeQualityViewStateModel) =
        MicroscopeQuality(
            viewModel.isDamaged,
            viewModel.magnification
        )
}
