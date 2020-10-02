package net.aiscope.gdd_app.repository

import org.junit.Test

class UUIDTest {
    private val subject = UUID

    @Test
    fun `should generate a uuid`() {
        val uuid = subject.generateUUID()
        val uuid2 = subject.generateUUID()

        assert(uuid != uuid2)
    }

    @Test
    fun `should generate a uuid format`() {
        val pattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex()
        val uuid = subject.generateUUID()

        assert(uuid.matches(pattern))
    }
}
