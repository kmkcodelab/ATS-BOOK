package com.pocketdev.ats_book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pocketdev.ats_book.data.repository.PreferencesRepository
import com.pocketdev.ats_book.data.repository.StoryRepository
import com.pocketdev.ats_book.ui.bookmarks.BookmarksScreen
import com.pocketdev.ats_book.ui.home.HomeScreen
import com.pocketdev.ats_book.ui.navigation.Screen
import com.pocketdev.ats_book.ui.reader.ReaderScreen
import com.pocketdev.ats_book.ui.theme.ATSBookTheme

class MainActivity : ComponentActivity() {

    private lateinit var storyRepository: StoryRepository
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storyRepository = StoryRepository(applicationContext)
        preferencesRepository = PreferencesRepository(applicationContext)

        setContent {
            val systemInDark = isSystemInDarkTheme()
            val initialDark = remember {
                if (preferencesRepository.hasCustomDarkModePreference()) {
                    preferencesRepository.getDarkModePreference()
                } else {
                    systemInDark
                }
            }
            var isDarkMode by remember { mutableStateOf(initialDark) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

            // Track bookmark updates to trigger recomposition when bookmarks change
            var bookmarkRefreshTrigger by remember { mutableStateOf(0) }

            val toggleTheme: () -> Unit = {
                val newDark = !isDarkMode
                isDarkMode = newDark
                preferencesRepository.saveDarkModePreference(newDark)
            }

            ATSBookTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            key(bookmarkRefreshTrigger) {
                                HomeScreen(
                                    repository = storyRepository,
                                    preferencesRepository = preferencesRepository,
                                    onStoryClick = { story ->
                                        currentScreen = Screen.Reader(story.id)
                                    },
                                    onNavigateToBookmarks = {
                                        currentScreen = Screen.Bookmarks
                                    },
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = toggleTheme
                                )
                            }
                        }

                        is Screen.Reader -> {
                            val story = remember(screen.storyId) {
                                storyRepository.getStoryById(screen.storyId)
                            }

                            // Mark as last read story
                            LaunchedEffect(screen.storyId) {
                                preferencesRepository.saveLastReadStoryId(screen.storyId)
                            }

                            var isBookmarked by remember(screen.storyId, bookmarkRefreshTrigger) {
                                mutableStateOf(preferencesRepository.isStoryBookmarked(screen.storyId))
                            }

                            var fontSizeScale by remember {
                                mutableStateOf(preferencesRepository.getFontSizeScale())
                            }

                            BackHandler {
                                currentScreen = Screen.Home
                            }

                            if (story != null) {
                                ReaderScreen(
                                    story = story,
                                    onBackClick = { currentScreen = Screen.Home },
                                    isBookmarked = isBookmarked,
                                    onToggleBookmark = {
                                        val nowBookmarked = preferencesRepository.toggleBookmark(story.id)
                                        isBookmarked = nowBookmarked
                                        bookmarkRefreshTrigger++
                                    },
                                    fontSizeMultiplier = fontSizeScale,
                                    onFontSizeChange = { newScale ->
                                        fontSizeScale = newScale
                                        preferencesRepository.saveFontSizeScale(newScale)
                                    },
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = toggleTheme
                                )
                            } else {
                                currentScreen = Screen.Home
                            }
                        }

                        is Screen.Bookmarks -> {
                            val bookmarkedStories = remember(bookmarkRefreshTrigger) {
                                val savedIds = preferencesRepository.getBookmarkedStoryIds()
                                storyRepository.getAllStories().filter { savedIds.contains(it.id) }
                            }

                            BackHandler {
                                currentScreen = Screen.Home
                            }

                            BookmarksScreen(
                                bookmarkedStories = bookmarkedStories,
                                onStoryClick = { story ->
                                    currentScreen = Screen.Reader(story.id)
                                },
                                onBackClick = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
