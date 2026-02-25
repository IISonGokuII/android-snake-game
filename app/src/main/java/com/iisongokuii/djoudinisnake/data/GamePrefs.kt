package com.iisongokuii.djoudinisnake.data

import android.content.Context
import android.content.SharedPreferences

class GamePrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("djoudini_prefs", Context.MODE_PRIVATE)

    fun getHighScore(mode: String): Int {
        return prefs.getInt("highscore_$mode", 0)
    }

    fun getHighScoreName(mode: String): String {
        return prefs.getString("highscore_name_$mode", "Unknown Illusionist") ?: "Unknown Illusionist"
    }

    fun saveHighScore(mode: String, score: Int, name: String) {
        val current = getHighScore(mode)
        if (score >= current) { // Allow overriding if score is same but maybe different name, or better >=
            prefs.edit()
                .putInt("highscore_$mode", score)
                .putString("highscore_name_$mode", name.ifBlank { "Djoudini" })
                .apply()
        }
    }

    fun isHapticsEnabled(): Boolean {
        return prefs.getBoolean("haptics_enabled", true)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
    }
}
