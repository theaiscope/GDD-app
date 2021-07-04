package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataMapper
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

    }

    @Test
    fun shouldReturnSmearType() {
        assertEquals(SmearType.THICK, MetadataMapper.getSmearType(R.id.metadata_blood_smear_thick))
        assertEquals(SmearType.THIN, MetadataMapper.getSmearType(R.id.metadata_blood_smear_thin))
    }

    @Test
    fun shouldReturnSmearTypeId() {
        assertEquals(
            R.id.metadata_blood_smear_thick,
            MetadataMapper.getSmearTypeId(SmearType.THICK)
        )
        assertEquals(R.id.metadata_blood_smear_thin, MetadataMapper.getSmearTypeId(SmearType.THIN))
    }

    @Test
    fun shouldReturnSpecies() {
        assertEquals(
            MalariaSpecies.P_FALCIPARUM,
            MetadataMapper.getSpecies(context, "P. falciparum")
        )
        assertEquals(MalariaSpecies.P_VIVAX, MetadataMapper.getSpecies(context, "P. vivax"))
        assertEquals(MalariaSpecies.P_OVALE, MetadataMapper.getSpecies(context, "P. ovale"))
        assertEquals(MalariaSpecies.P_MALARIAE, MetadataMapper.getSpecies(context, "P. malariae"))
        assertEquals(MalariaSpecies.P_KNOWLESI, MetadataMapper.getSpecies(context, "P. knowlesi"))
    }

    @Test
    fun shouldReturnSpeciesValue() {
        assertEquals(
            "P. falciparum",
            MetadataMapper.getSpeciesValue(context, MalariaSpecies.P_FALCIPARUM)
        )
        assertEquals("P. vivax", MetadataMapper.getSpeciesValue(context, MalariaSpecies.P_VIVAX))
        assertEquals("P. ovale", MetadataMapper.getSpeciesValue(context, MalariaSpecies.P_OVALE))
        assertEquals(
            "P. malariae",
            MetadataMapper.getSpeciesValue(context, MalariaSpecies.P_MALARIAE)
        )
        assertEquals(
            "P. knowlesi",
            MetadataMapper.getSpeciesValue(context, MalariaSpecies.P_KNOWLESI)
        )
    }
}
