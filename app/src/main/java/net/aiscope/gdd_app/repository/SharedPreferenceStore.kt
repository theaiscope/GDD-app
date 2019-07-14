package net.aiscope.gdd_app.repository

import android.app.Activity
import android.content.Context
import javax.inject.Inject

class SharedPreferenceStore @Inject constructor(val context: Context) {
    private val PREF_NAME = "AIScopeStore"
    val sharedPreference = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)

    fun store(id: String, data: String) {
        val editor = sharedPreference.edit()
        editor.putString(id, data)
        editor.apply()
    }

    fun load(id: String): String {
        return sharedPreference.getString(id, "") as String
    }

    fun all(): Collection<String> {
        return sharedPreference.all.values as Collection<String>
    }
}