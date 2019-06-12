package net.aiscope.gdd_app.repository

import android.content.Context
import android.preference.PreferenceManager
import net.aiscope.gdd_app.model.HealthFacility

class SharedPreferencesRepository(private val context: Context) : HospitalRepository {
    override fun store(hosptial: HealthFacility) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("Hospital_Name", hosptial.name)
        editor.putString("Hospital_Id", hosptial.id)
        editor.apply()
    }

    override fun load(): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString("Hospital_Name", "not found")!!

    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}