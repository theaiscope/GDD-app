package net.aiscope.gdd_app.ui.sample_preparation

import android.content.Context
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.WaterType

object SamplePreparationMapper {

    private fun getWaterType(waterTypeValue: String, context: Context): WaterType {
        return when (waterTypeValue) {
            context.getString(R.string.water_type_distilled) -> WaterType.DISTILLED
            context.getString(R.string.water_type_bottled) -> WaterType.BOTTLED
            context.getString(R.string.water_type_tap) -> WaterType.TAP
            context.getString(R.string.water_type_well) -> WaterType.WELL
            else -> throw IllegalStateException("$waterTypeValue water type is unknown")
        }
    }

    private fun getWaterTypeValue(waterType: WaterType?, context: Context): String {
        return when (waterType) {
            WaterType.DISTILLED -> context.getString(R.string.water_type_distilled)
            WaterType.BOTTLED -> context.getString(R.string.water_type_bottled)
            WaterType.TAP -> context.getString(R.string.water_type_tap)
            WaterType.WELL -> context.getString(R.string.water_type_well)
            null -> ""
        }
    }

    fun convert(model: SamplePreparation?, context: Context) =
        if (model == null)
            null
        else
            SamplePreparationViewStateModel(
                this.getWaterTypeValue(model.waterType, context),
                model.usesGiemsa,
                model.giemsaFP,
                model.usesPbs,
                model.reusesSlides
            )

    fun convert(viewModel: SamplePreparationViewStateModel, context: Context) =
        SamplePreparation(
            this.getWaterType(viewModel.waterType, context),
            viewModel.usesGiemsa,
            viewModel.giemsaFP,
            viewModel.usesPbs,
            viewModel.reusesSlides
        )
}
