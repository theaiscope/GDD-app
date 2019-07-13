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
class UUIDTest {
    val subject = UUID

    @Test
    fun `should generate a uuid`() {
        val uuid = subject.generateUUID()
        val uuid2 = subject.generateUUID()

        assert(uuid != uuid2)
    }

    @Test
    fun `should generate a uuid format`() {
        val pattern = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}".toRegex()
        val uuid = subject.generateUUID()

        assert(uuid.matches(pattern))
    }
}
