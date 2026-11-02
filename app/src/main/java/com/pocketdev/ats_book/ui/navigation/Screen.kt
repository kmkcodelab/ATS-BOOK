package com.pocketdev.ats_book.ui.navigation

sealed class Screen {
    object Home : Screen()
    data class Reader(val storyId: Int) : Screen()
    object Bookmarks : Screen()
}
