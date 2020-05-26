package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.Microscopist

interface MicroscopistRepository {

    fun load(): Microscopist
    fun store(microscopist: Microscopist)
}
