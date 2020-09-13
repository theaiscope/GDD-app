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
    private val DISEASE = "malaria"

    private val sampleOnlyRequired = Sample(ID, HOSPITAL_ID, MICROSCOPIST, DISEASE)
    private val sampleOnlyRequiredJson = """{"id":"1111","healthFacility":"H_St_Pau","status":"Incomplete","disease":"malaria"""

    @Before
    fun before() = coroutinesTestRule.runBlockingTest {
        whenever(healthFacilityRepository.load()).thenReturn(HealthFacility(HOSPITAL_NAME, HOSPITAL_ID, MICROSCOPIST))
        whenever(gson.toJson(any<SampleDto>())).thenReturn(sampleOnlyRequiredJson)
        whenever(gson.fromJson<SampleDto>(eq(sampleOnlyRequiredJson), any<Type>())).thenReturn(sampleOnlyRequired.toDto())
    }

    @Test
    fun `should store a sample with required fields`() {
        val beforeCreate = Calendar.getInstance()
        beforeCreate.add(Calendar.SECOND, -1)

        val stored = subject.store(sampleOnlyRequired)

        argumentCaptor<String>().apply {
            verify(store).store(eq(ID), capture())
            assert(firstValue == sampleOnlyRequiredJson)
        }

        assert(stored.lastModified.after(beforeCreate))
        assert(stored.lastModified.after(stored.createdOn))
    }

    @Test
    fun `should load a sample with required fields`() {
        whenever(store.load(ID)).thenReturn(sampleOnlyRequiredJson)

        val sample = subject.load(ID)

        assert(sample == sampleOnlyRequired)
    }

    @Test
    fun `should create a new sample`() = coroutinesTestRule.runBlockingTest {
        val beforeCreate = Calendar.getInstance()
        beforeCreate.add(Calendar.SECOND, -1)

        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"
        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        val sample = subject.create(DISEASE)

        val afterCreate = Calendar.getInstance()
        afterCreate.add(Calendar.SECOND, 1)

        assert(sample.healthFacility == HOSPITAL_ID)
        assert(sample.disease == DISEASE)
        assert(sample.id == uuid)
        assert(sample.createdOn.after(beforeCreate))
        assert(sample.createdOn.before(afterCreate))
        assert(sample.lastModified.after(beforeCreate))
        assert(sample.lastModified.before(afterCreate))
    }

    @Test
    fun `after creating a new sample it should be one returned when current`() = coroutinesTestRule.runBlockingTest {
        val uuid = "d9255e5d-5c68-4245-b7c9-da0964116cce"

        whenever(uuidGenerator.generateUUID()).thenReturn(uuid)

        subject.create(DISEASE)
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
    fun `should give last stored sample`() = coroutinesTestRule.runBlockingTest {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        val (todaySample, todaySampleJson) = mockSampleAndJson("1112", today, SampleStatus.ReadyToUpload)
        val (_, yesterdaySampleJson) = mockSampleAndJson("1113", yesterday, SampleStatus.Incomplete)

        whenever(store.all()).thenReturn(listOf(sampleOnlyRequiredJson, todaySampleJson, yesterdaySampleJson))

        val sample = subject.lastSaved()

        assert(sample == todaySample)
    }

    @Test
    fun `should give last unfinished sample on current`() = coroutinesTestRule.runBlockingTest {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val twoDays = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }

        val (_, todaySampleJson) = mockSampleAndJson("1112", today, SampleStatus.ReadyToUpload)
        val (yesterdaySample, yesterdaySampleJson) = mockSampleAndJson("1113", yesterday, SampleStatus.Incomplete)
        val (_, twoDaysSampleJson) = mockSampleAndJson("1114", twoDays, SampleStatus.Incomplete)

        whenever(store.all()).thenReturn(listOf(todaySampleJson, yesterdaySampleJson, twoDaysSampleJson))

        val sample = subject.current()

        assert(sample == yesterdaySample)
    }

    private fun mockSampleAndJson(id: String, date: Calendar, status: SampleStatus): Pair<Sample, String> {
        val sample = Sample(id, HOSPITAL_ID, MICROSCOPIST, DISEASE, status = status, createdOn = date)
        val sampleJson = """{"id":"$id","healthFacility":"H_St_Pau","status":"$status","createdOn":"$date"}"""
        whenever(gson.fromJson<SampleDto>(eq(sampleJson), any<Type>())).thenReturn(sample.toDto())

        return sample to sampleJson
    }
}
