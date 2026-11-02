package com.pocketdev.ats_book.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.ats_book.data.model.Category
import com.pocketdev.ats_book.data.model.Story
import com.pocketdev.ats_book.data.repository.PreferencesRepository
import com.pocketdev.ats_book.data.repository.StoryRepository
import com.pocketdev.ats_book.ui.components.CategoryChip
import com.pocketdev.ats_book.ui.components.FeaturedBanner
import com.pocketdev.ats_book.ui.components.StoryCard
import com.pocketdev.ats_book.ui.components.StorySearchBar
import com.pocketdev.ats_book.ui.components.UnlockStoryDialog
import com.pocketdev.ats_book.ui.theme.ATSBookTheme

@Composable
fun HomeScreen(
    repository: StoryRepository,
    preferencesRepository: PreferencesRepository,
    onStoryClick: (Story) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    var pendingStoryToUnlock by remember { mutableStateOf<Story?>(null) }

    val featuredStory = remember { repository.getFeaturedStory() }
    val bookmarkedIds = remember { preferencesRepository.getBookmarkedStoryIds() }

    // Check last read story
    val lastReadStoryId = remember { preferencesRepository.getLastReadStoryId() }
    val lastReadStory = remember(lastReadStoryId) {
        lastReadStoryId?.let { repository.getStoryById(it) }
    }

    // Filter stories based on selected category and search query
    val filteredStories = remember(searchQuery, selectedCategory) {
        repository.searchStories(query = searchQuery, category = selectedCategory)
    }

    // Story click with unlock check
    val handleStoryClick: (Story) -> Unit = { story ->
        val isUnlocked = preferencesRepository.isStoryUnlocked(story.id, story.isLocked)
        if (isUnlocked) {
            onStoryClick(story)
        } else {
            pendingStoryToUnlock = story
        }
    }

    // Unlock Dialog if user tapped locked story
    pendingStoryToUnlock?.let { story ->
        UnlockStoryDialog(
            story = story,
            onUnlock = {
                preferencesRepository.unlockStory(story.id)
                pendingStoryToUnlock = null
                onStoryClick(story)
            },
            onDismiss = { pendingStoryToUnlock = null }
        )
    }

    HomeContent(
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        selectedCategory = selectedCategory,
        onSelectCategory = { selectedCategory = it },
        featuredStory = featuredStory,
        lastReadStory = lastReadStory,
        filteredStories = filteredStories,
        bookmarkCount = bookmarkedIds.size,
        onStoryClick = handleStoryClick,
        onNavigateToBookmarks = onNavigateToBookmarks,
        isDarkMode = isDarkMode,
        onToggleDarkMode = onToggleDarkMode,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    featuredStory: Story?,
    lastReadStory: Story?,
    filteredStories: List<Story>,
    bookmarkCount: Int,
    onStoryClick: (Story) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                bookmarkCount = bookmarkCount,
                onNavigateToBookmarks = onNavigateToBookmarks,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar item
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    StorySearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange
                    )
                }
            }

            // Continue Reading Banner (if user has a recent story and not searching)
            if (searchQuery.isBlank() && lastReadStory != null) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ContinueReadingCard(
                            story = lastReadStory,
                            onClick = { onStoryClick(lastReadStory) }
                        )
                    }
                }
            }

            // Categories horizontal scroll
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "বিভাগসমূহ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(Category.ALL) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategory.equals(category.id, ignoreCase = true),
                                onSelect = { onSelectCategory(category.id) }
                            )
                        }
                    }
                }
            }

            // Featured Story Banner (show only if not searching)
            if (searchQuery.isBlank() && selectedCategory.equals("all", ignoreCase = true) && featuredStory != null) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FeaturedBanner(
                            story = featuredStory,
                            onClick = { onStoryClick(featuredStory) }
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "অনুসন্ধানের ফলাফল" else "গল্পের তালিকা",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${filteredStories.size} টি গল্প",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Empty state if no results
            if (filteredStories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📖",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "কোনো গল্প পাওয়া যায়নি",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // List of Story Cards
                items(
                    items = filteredStories,
                    key = { it.id }
                ) { story ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        StoryCard(
                            story = story,
                            onClick = { onStoryClick(story) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContinueReadingCard(
    story: Story,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📖 পড়া চালিয়ে যান",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${story.author} • ${story.readTime}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "পড়ুন",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    bookmarkCount: Int,
    onNavigateToBookmarks: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ats book",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "অফলাইন বাংলা গল্পের ভাণ্ডার",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bookmarks Button with Badge
                IconButton(
                    onClick = onNavigateToBookmarks,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    BadgedBox(
                        badge = {
                            if (bookmarkCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(text = "$bookmarkCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "🔖",
                            fontSize = 16.sp
                        )
                    }
                }

                // Dark Mode Toggle Button
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = if (isDarkMode) "☀️" else "🌙",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// ======================= PREVIEWS =======================

private val previewSampleStories = listOf(
    Story(
        id = 1,
        title = "বৃষ্টিভেজা বিকেলে এক কাপ চা",
        category = "Romance",
        author = "অনিন্দ্য রায়",
        content = "বাইরে তখন ঝুম বৃষ্টি নামছিল। জানালার কাঁচ বেয়ে গড়িয়ে পড়া জলের ফোঁটাগুলো যেন কোনো পুরোনো স্মৃতি বয়ে নিয়ে আসছিল।",
        isLocked = false,
        readTime = "3 min"
    ),
    Story(
        id = 2,
        title = "পুরোনো বাংলোর ছায়া",
        category = "Horror",
        author = "সৈকত বন্দ্যোপাধ্যায়",
        content = "পাহাড়ের চূড়ায় অবস্থিত শতাব্দী প্রাচীন কাঠের বাংলোটি সম্পর্কে গ্রামে নানা অদ্ভুত গল্প প্রচলিত ছিল।",
        isLocked = false,
        readTime = "4 min"
    ),
    Story(
        id = 6,
        title = "নীল খামের শেষ চিঠি",
        category = "Romance",
        author = "তানভীর আহমেদ",
        content = "ক্যাম্পাসের পুরোনো লাইব্রেরির সবচেয়ে ধুলোবালি পড়া কোণে বসে চিঠিটা লিখছিল আরিয়ান।",
        isLocked = true,
        readTime = "6 min"
    )
)

@Preview(name = "Home Screen - Light Mode", showBackground = true)
@Composable
fun HomeScreenPreview() {
    ATSBookTheme(darkTheme = false) {
        HomeContent(
            searchQuery = "",
            onSearchQueryChange = {},
            selectedCategory = "all",
            onSelectCategory = {},
            featuredStory = previewSampleStories[0],
            lastReadStory = previewSampleStories[1],
            filteredStories = previewSampleStories,
            bookmarkCount = 2,
            onStoryClick = {},
            onNavigateToBookmarks = {},
            isDarkMode = false,
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Home Screen - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreenDarkPreview() {
    ATSBookTheme(darkTheme = true) {
        HomeContent(
            searchQuery = "",
            onSearchQueryChange = {},
            selectedCategory = "all",
            onSelectCategory = {},
            featuredStory = previewSampleStories[0],
            lastReadStory = previewSampleStories[1],
            filteredStories = previewSampleStories,
            bookmarkCount = 2,
            onStoryClick = {},
            onNavigateToBookmarks = {},
            isDarkMode = true,
            onToggleDarkMode = {}
        )
    }
}
