package com.pocketdev.ats_book.data.repository

import android.content.Context
import android.content.SharedPreferences

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ats_book_preferences"
        private const val KEY_BOOKMARKS = "bookmarked_story_ids"
        private const val KEY_UNLOCKED = "unlocked_story_ids"
        private const val KEY_LAST_READ = "last_read_story_id"
        private const val KEY_FONT_SIZE = "font_size_scale"
        private const val KEY_DARK_MODE = "is_dark_mode"
        private const val KEY_DARK_MODE_SET = "is_dark_mode_set"
    }

    // --- Bookmarks Management ---

    fun getBookmarkedStoryIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_BOOKMARKS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isStoryBookmarked(storyId: Int): Boolean {
        return getBookmarkedStoryIds().contains(storyId)
    }

    fun toggleBookmark(storyId: Int): Boolean {
        val current = getBookmarkedStoryIds().toMutableSet()
        val isNowBookmarked = if (current.contains(storyId)) {
            current.remove(storyId)
            false
        } else {
            current.add(storyId)
            true
        }
        prefs.edit().putStringSet(KEY_BOOKMARKS, current.map { it.toString() }.toSet()).apply()
        return isNowBookmarked
    }

    // --- Unlocked Stories (Rewarded Ad Monetization) ---

    fun getUnlockedStoryIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_UNLOCKED, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isStoryUnlocked(storyId: Int, isDefaultLocked: Boolean): Boolean {
        if (!isDefaultLocked) return true
        return getUnlockedStoryIds().contains(storyId)
    }

    fun unlockStory(storyId: Int) {
        val current = getUnlockedStoryIds().toMutableSet()
        current.add(storyId)
        prefs.edit().putStringSet(KEY_UNLOCKED, current.map { it.toString() }.toSet()).apply()
    }

    // --- Last Read Story ---

    fun getLastReadStoryId(): Int? {
        val id = prefs.getInt(KEY_LAST_READ, -1)
        return if (id != -1) id else null
    }

    fun saveLastReadStoryId(storyId: Int) {
        prefs.edit().putInt(KEY_LAST_READ, storyId).apply()
    }

    // --- Reader Font Size Scale ---

    fun getFontSizeScale(): Float {
        return prefs.getFloat(KEY_FONT_SIZE, 1.0f)
    }

    fun saveFontSizeScale(scale: Float) {
        prefs.edit().putFloat(KEY_FONT_SIZE, scale).apply()
    }

    // --- Dark Mode ---

    fun hasCustomDarkModePreference(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE_SET, false)
    }

    fun getDarkModePreference(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveDarkModePreference(isDark: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_MODE, isDark)
            .putBoolean(KEY_DARK_MODE_SET, true)
            .apply()
    }
}
