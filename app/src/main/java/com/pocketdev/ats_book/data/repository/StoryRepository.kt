package com.pocketdev.ats_book.data.repository

import android.content.Context
import com.pocketdev.ats_book.data.model.Story
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class StoryRepository(private val context: Context) {

    private var cachedStories: List<Story>? = null

    fun getAllStories(): List<Story> {
        cachedStories?.let { return it }

        val stories = mutableListOf<Story>()
        try {
            val inputStream = context.assets.open("stories.json")
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val story = Story(
                    id = obj.optInt("id", i + 1),
                    title = obj.optString("title", ""),
                    category = obj.optString("category", ""),
                    author = obj.optString("author", ""),
                    content = obj.optString("content", ""),
                    isLocked = obj.optBoolean("isLocked", false),
                    readTime = obj.optString("readTime", "5 min")
                )
                stories.add(story)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cachedStories = stories
        return stories
    }

    fun getStoryById(id: Int): Story? {
        return getAllStories().find { it.id == id }
    }

    fun getStoriesByCategory(category: String): List<Story> {
        if (category.equals("all", ignoreCase = true)) {
            return getAllStories()
        }
        return getAllStories().filter { it.category.equals(category, ignoreCase = true) }
    }

    fun searchStories(query: String, category: String = "all"): List<Story> {
        val baseList = getStoriesByCategory(category)
        if (query.isBlank()) return baseList

        val trimmed = query.trim().lowercase()
        return baseList.filter {
            it.title.lowercase().contains(trimmed) ||
            it.author.lowercase().contains(trimmed) ||
            it.category.lowercase().contains(trimmed)
        }
    }

    fun getFeaturedStory(): Story? {
        val stories = getAllStories()
        return stories.firstOrNull { !it.isLocked } ?: stories.firstOrNull()
    }
}
