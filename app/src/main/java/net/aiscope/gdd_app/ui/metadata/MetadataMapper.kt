package net.aiscope.gdd_app.ui.metadata

import android.content.Context
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.SmearType

object MetadataMapper {
    fun getSmearType(smearTypeId: Int): SmearType {
        return when (smearTypeId) {
            R.id.metadata_blood_smear_thick -> SmearType.THICK
            R.id.metadata_blood_smear_thin -> SmearType.THIN
            else -> throw IllegalStateException(
                "$smearTypeId smearTypeId is unknown"
            )
        }
    }

    fun getSmearTypeId(smearType: SmearType): Int {
        return when (smearType) {
            SmearType.THICK -> R.id.metadata_blood_smear_thick
            SmearType.THIN -> R.id.metadata_blood_smear_thin
        }
    }

    fun getSpecies(context: Context, speciesValue: String): MalariaSpecies {
        return when (speciesValue) {
            context.getString(R.string.malaria_species_p_falciparum) -> MalariaSpecies.P_FALCIPARUM
            context.getString(R.string.malaria_species_p_vivax) -> MalariaSpecies.P_VIVAX
            context.getString(R.string.malaria_species_p_ovale) -> MalariaSpecies.P_OVALE
            context.getString(R.string.malaria_species_p_malariae) -> MalariaSpecies.P_MALARIAE
            context.getString(R.string.malaria_species_p_knowlesi) -> MalariaSpecies.P_KNOWLESI
            else -> throw IllegalStateException(
                "$speciesValue species is unknown"
            )
        }
    }

    fun getSpeciesValue(context: Context, species: MalariaSpecies): String {
        return when (species) {
            MalariaSpecies.P_FALCIPARUM -> context.getString(R.string.malaria_species_p_falciparum)
            MalariaSpecies.P_VIVAX -> context.getString(R.string.malaria_species_p_vivax)
            MalariaSpecies.P_OVALE -> context.getString(R.string.malaria_species_p_ovale)
            MalariaSpecies.P_MALARIAE -> context.getString(R.string.malaria_species_p_malariae)
            MalariaSpecies.P_KNOWLESI -> context.getString(R.string.malaria_species_p_knowlesi)
        }
    }

    fun getStage(context: Context, stageValue: String): MalariaStage {
        return when (stageValue) {
            context.getString(R.string.malaria_stage_ring) -> MalariaStage.RING
            context.getString(R.string.malaria_stage_trophozoite) -> MalariaStage.TROPHOZOITE
            context.getString(R.string.malaria_stage_schizont) -> MalariaStage.SCHIZONT
            context.getString(R.string.malaria_stage_gametocyte) -> MalariaStage.GAMETOCYTE
            else -> throw IllegalStateException(
                "$stageValue stage is unknown"
            )
        }
    }
}
