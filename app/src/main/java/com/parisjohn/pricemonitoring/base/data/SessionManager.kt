package com.parisjohn.pricemonitoring.base.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext private var context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    companion object {
        const val USER_TOKEN = "user_token"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String?) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.commit()
    }

    fun clearSession(){
        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}