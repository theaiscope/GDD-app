package net.aiscope.gdd_app.repository

interface HospitalRepository {
    fun store(hospitalName: String)
    fun load(): String
    fun delete()
}