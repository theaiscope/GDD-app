package net.aiscope.gdd_app.repository

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.HealthFacility
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleStatus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.reflect.Type
import java.util.Calendar

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SampleRepositorySharedPreferenceTest {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    @Mock
    lateinit var store: SharedPreferenceStore

    @Mock
    lateinit var healthFacilityRepository: HealthFacilityRepository

    @Mock
    lateinit var uuidGenerator: UUID

    @Mock
    lateinit var gson: Gson

    @InjectMocks
    lateinit var subject: SampleRepositorySharedPreference

    private val ID = "1111"
    private val HOSPITAL_NAME = "H. St. Pau"
    private val HOSPITAL_ID = "H_St_Pau"
    private val MICROSCOPIST = "a microscopist"

    private val sampleOnlyRequired = Sample(ID, HOSPITAL_ID, MICROSCOPIST)
    private val sampleOnlyRequiredJson = """{"id":"1111","healthFacility":"H_St_Pau","status":"Incomplete"""

    @Before
    fun before() = coroutinesTestRule.runBlockingTest {
        whenever(healthFacilityRepository.load()).thenReturn(HealthFacility(HOSPITAL_NAME, HOSPITAL_ID, MICROSCOPIST))
        whenever(gson.toJson(sampleOnlyRequired.toDto())).thenReturn(sampleOnlyRequiredJson)
        whenever(gson.fromJson<SampleDto>(eq(sampleOnlyRequiredJson), any<Type>())).thenReturn(sampleOnlyRequired.toDto())
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
    fun `should create a new sample`() = coroutinesTestRule.runBlockingTest {
        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"
        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        val sample = subject.create()

        assert(sample.healthFacility == HOSPITAL_ID)
        assert(sample.id == uuid)
    }

    @Test
    fun `after creating a new sample it should be one returned when current`() = coroutinesTestRule.runBlockingTest {
        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"

        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        subject.create()
        val sample = subject.current()

        assert(sample.id == uuid)
    }

    @Test
    fun `after loading a new sample it should be one returned when current`() = coroutinesTestRule.runBlockingTest {
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

    @Test
    fun `should give last sample`() = coroutinesTestRule.runBlockingTest {
        val todayId = "1112"
        val yesterdayId = "1113"
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val todaySample = Sample(todayId, HOSPITAL_ID, MICROSCOPIST, status = SampleStatus.ReadyToUpload, createdOn = today)
        val todaySampleJson = """{"id":"1112","healthFacility":"H_St_Pau","status":"ReadyToUpload","createdOn":"$today"}"""
        val yesterdaySample = Sample(yesterdayId, HOSPITAL_ID, MICROSCOPIST, status = SampleStatus.ReadyToUpload, createdOn = yesterday)
        val yesterdaySampleJson = """{"id":"1113","healthFacility":"H_St_Pau","status":"ReadyToUpload","createdOn":"$yesterday"}"""

        whenever(gson.fromJson<SampleDto>(eq(todaySampleJson), any<Type>())).thenReturn(todaySample.toDto())
        whenever(gson.fromJson<SampleDto>(eq(yesterdaySampleJson), any<Type>())).thenReturn(yesterdaySample.toDto())

        whenever(store.all()).thenReturn(listOf(sampleOnlyRequiredJson, todaySampleJson, yesterdaySampleJson))

        val sample = subject.last()

        assert(sample == todaySample)
    }
}
