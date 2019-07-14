package net.aiscope.gdd_app.repository

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.model.HealthFacility
import net.aiscope.gdd_app.model.Sample
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SampleRepositorySharedPreferenceTest {

    @Mock
    lateinit var store: SharedPreferenceStore

    @Mock
    lateinit var hospitalRepository: HospitalRepository

    @Mock
    lateinit var uuidGenerator: UUID

    @InjectMocks
    lateinit var subject: SampleRepositorySharedPreference

    private val ID = "1111"
    private val HOSPITAL = "H. St. Pau"

    val sampleOnlyRequired = Sample(ID, HOSPITAL)
    val sampleOnlyRequiredJson = """{"id":"1111","healthFacility":"H. St. Pau","status":"Incomplete"}"""

    @Before
    fun before() {
        whenever(hospitalRepository.load()).thenReturn(HOSPITAL)
    }

    @Test
    fun `should store a sample with required fields`() {
        subject.store(sampleOnlyRequired)

        argumentCaptor<String>().apply {
            verify(store).store(eq(ID), capture())
            assert(firstValue == sampleOnlyRequiredJson)
        }
    }

    @Test
    fun `should load a sample with required fields`() {
        whenever(store.load(ID)).thenReturn(sampleOnlyRequiredJson)

        val sample = subject.load(ID)

        assert(sample == sampleOnlyRequired)
    }

    @Test
    fun `should create a new sample`() {
        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"
        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        val sample = subject.create()

        assert(sample.healthFacility == HOSPITAL)
        assert(sample.id == uuid)
    }

    @Test
    fun `after creating a new sample it should be one returned when current`() {
        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"

        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        subject.create()
        val sample = subject.current()

        assert(sample.id == uuid)
    }

    @Test
    fun `after loading a new sample it should be one returned when current`() {
        whenever(store.load(ID)).thenReturn(sampleOnlyRequiredJson)

        subject.load(ID)
        val sample = subject.current()

        assert(sample == sampleOnlyRequired)
    }

    @Test
    fun `should give all the samples`() {
        whenever(store.all()).thenReturn(listOf(sampleOnlyRequiredJson, sampleOnlyRequiredJson))

        val samples = subject.all()

        assert(samples.size == 2)
    }
}
