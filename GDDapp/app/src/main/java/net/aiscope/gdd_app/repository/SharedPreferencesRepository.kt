package net.aiscope.gdd_app.repository

import android.content.Context
import android.preference.PreferenceManager

class SharedPreferencesRepository(val context: Context) : HospitalRepository {
    override fun store(hospitalName: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("Hospital_Name", hospitalName)
        editor.apply()
    }

    override fun load(): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString("Hospital_Name", "not found") ?: "bad error"


    override fun delete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}