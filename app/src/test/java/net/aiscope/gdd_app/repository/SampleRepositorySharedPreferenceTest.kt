package net.aiscope.gdd_app.repository

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Sample
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SampleRepositorySharedPreferenceTest {

    @Mock
    lateinit var store: SharedPreferenceStore

    @InjectMocks
    lateinit var subject: SampleRepositorySharedPreference

    private val ID = "1111"

    val sampleOnlyRequired = Sample(ID, "malaria", null, null)
    val sampleOnlyRequiredJson = """{"id":"${ID}","disease":"malaria"}"""


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
}
