package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.ui.metadata.MetadataMapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MetadataMapperTest {
    @Mock
    lateinit var context: Context

    @Before
    fun before() {
        whenever(context.getString(R.string.malaria_species_p_falciparum))
            .thenReturn("P. falciparum")
        whenever(context.getString(R.string.malaria_species_p_vivax))
            .thenReturn("P. vivax")
        whenever(context.getString(R.string.malaria_species_p_ovale))
            .thenReturn("P. ovale")
        whenever(context.getString(R.string.malaria_species_p_malariae))
            .thenReturn("P. malariae")
        whenever(context.getString(R.string.malaria_species_p_knowlesi))
            .thenReturn("P. knowlesi")

        whenever(context.getString(R.string.malaria_stage_ring))
            .thenReturn("Ring")
        whenever(context.getString(R.string.malaria_stage_trophozoite))
            .thenReturn("Trophozoite")
        whenever(context.getString(R.string.malaria_stage_schizont))
            .thenReturn("Schizont")
        whenever(context.getString(R.string.malaria_stage_gametocyte))
            .thenReturn("Gametocyte")
    }

    @Test
    fun shouldReturnSmearType() {
            assertEquals(SmearType.THICK, MetadataMapper.getSmearType(R.id.metadata_blood_smear_thick))
            assertEquals(SmearType.THIN, MetadataMapper.getSmearType(R.id.metadata_blood_smear_thin))
        }

    @Test
    fun shouldReturnSmearTypeId() {
            assertEquals(R.id.metadata_blood_smear_thick, MetadataMapper.getSmearTypeId(SampleMetadata(smearType = SmearType.THICK)))
            assertEquals(R.id.metadata_blood_smear_thin, MetadataMapper.getSmearTypeId(SampleMetadata(smearType = SmearType.THIN)))
        }

    @Test
    fun shouldReturnSpecies() {
            assertEquals(MalariaSpecies.P_FALCIPARUM, MetadataMapper.getSpecies(context,"P. falciparum"))
            assertEquals(MalariaSpecies.P_VIVAX, MetadataMapper.getSpecies(context,"P. vivax"))
            assertEquals(MalariaSpecies.P_OVALE, MetadataMapper.getSpecies(context,"P. ovale"))
            assertEquals(MalariaSpecies.P_MALARIAE, MetadataMapper.getSpecies(context,"P. malariae"))
            assertEquals(MalariaSpecies.P_KNOWLESI, MetadataMapper.getSpecies(context,"P. knowlesi"))
        }

    @Test
    fun shouldReturnSpeciesValue() {
            assertEquals("P. falciparum", MetadataMapper.getSpeciesValue(context, SampleMetadata(species = MalariaSpecies.P_FALCIPARUM)))
            assertEquals("P. vivax", MetadataMapper.getSpeciesValue(context, SampleMetadata(species = MalariaSpecies.P_VIVAX)))
            assertEquals("P. ovale", MetadataMapper.getSpeciesValue(context, SampleMetadata(species = MalariaSpecies.P_OVALE)))
            assertEquals("P. malariae", MetadataMapper.getSpeciesValue(context, SampleMetadata(species = MalariaSpecies.P_MALARIAE)))
            assertEquals("P. knowlesi", MetadataMapper.getSpeciesValue(context, SampleMetadata(species = MalariaSpecies.P_KNOWLESI)))
        }

    @Test
    fun shouldReturnStage() {
            assertEquals(MalariaStage.RING, MetadataMapper.getStage(context, "Ring"))
            assertEquals(MalariaStage.TROPHOZOITE, MetadataMapper.getStage(context, "Trophozoite"))
            assertEquals(MalariaStage.SCHIZONT, MetadataMapper.getStage(context, "Schizont"))
            assertEquals(MalariaStage.GAMETOCYTE, MetadataMapper.getStage(context, "Gametocyte"))
        }

    @Test
    fun shouldReturnStageValue() {
            assertEquals("Ring", MetadataMapper.getStageValue(context, SampleMetadata(stage = MalariaStage.RING)))
            assertEquals("Trophozoite", MetadataMapper.getStageValue(context, SampleMetadata(stage = MalariaStage.TROPHOZOITE)))
            assertEquals("Schizont", MetadataMapper.getStageValue(context, SampleMetadata(stage = MalariaStage.SCHIZONT)))
            assertEquals("Gametocyte", MetadataMapper.getStageValue(context, SampleMetadata(stage = MalariaStage.GAMETOCYTE)))
        }
}