package com.iisongokuii.djoudinisnake.data

import android.content.Context
import android.content.SharedPreferences

class GamePrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("djoudini_prefs", Context.MODE_PRIVATE)

    fun getHighScore(mode: String): Int {
        return prefs.getInt("highscore_$mode", 0)
    }

    fun saveHighScore(mode: String, score: Int) {
        val current = getHighScore(mode)
        if (score > current) {
            prefs.edit().putInt("highscore_$mode", score).apply()
        }
    }
}
