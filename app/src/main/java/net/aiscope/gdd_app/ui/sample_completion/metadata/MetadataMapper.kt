package net.aiscope.gdd_app.ui.sample_completion.metadata

import android.content.Context
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.SmearType

object MetadataMapper {
    fun getSmearType(smearTypeId: Int?): SmearType {
        return when (smearTypeId) {
            R.id.metadata_blood_smear_thick -> SmearType.THICK
            R.id.metadata_blood_smear_thin -> SmearType.THIN
            else -> error("$smearTypeId smearTypeId is unknown")
        }
    }

    fun getSmearTypeId(smearType: SmearType?): Int {
        return when (smearType) {
            SmearType.THICK -> R.id.metadata_blood_smear_thick
            SmearType.THIN -> R.id.metadata_blood_smear_thin
            null -> error("$smearType smearType is unknown"
            )
        }
    }

    fun getSpecies(context: Context, speciesValue: String?): MalariaSpecies {
        return when (speciesValue) {
            context.getString(R.string.malaria_species_p_falciparum) -> MalariaSpecies.P_FALCIPARUM
            context.getString(R.string.malaria_species_p_vivax) -> MalariaSpecies.P_VIVAX
            context.getString(R.string.malaria_species_p_ovale) -> MalariaSpecies.P_OVALE
            context.getString(R.string.malaria_species_p_malariae) -> MalariaSpecies.P_MALARIAE
            context.getString(R.string.malaria_species_p_knowlesi) -> MalariaSpecies.P_KNOWLESI
            else -> error("$speciesValue species is unknown")
        }
    }

    fun getSpeciesValue(context: Context, species: MalariaSpecies?): String {
        return when (species) {
            MalariaSpecies.P_FALCIPARUM -> context.getString(R.string.malaria_species_p_falciparum)
            MalariaSpecies.P_VIVAX -> context.getString(R.string.malaria_species_p_vivax)
            MalariaSpecies.P_OVALE -> context.getString(R.string.malaria_species_p_ovale)
            MalariaSpecies.P_MALARIAE -> context.getString(R.string.malaria_species_p_malariae)
            MalariaSpecies.P_KNOWLESI -> context.getString(R.string.malaria_species_p_knowlesi)
            null ->  ""
        }
    }
}
