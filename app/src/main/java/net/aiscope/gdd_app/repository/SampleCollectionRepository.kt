package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleCollection

interface SampleCollectionRepository {
    fun store(sampleCollection: SampleCollection)
    fun store(sample: Sample)
}