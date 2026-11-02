package com.pocketdev.ats_book.ui.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.ats_book.data.model.Story
import com.pocketdev.ats_book.ui.components.StoryCard
import com.pocketdev.ats_book.ui.theme.ATSBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    bookmarkedStories: List<Story>,
    onStoryClick: (Story) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BookmarksTopBar(
                count = bookmarkedStories.size,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (bookmarkedStories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔖",
                        fontSize = 54.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "কোনো সংরক্ষিত গল্প নেই",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "আপনার পছন্দের গল্পগুলো পরবর্তীতে সহজে পড়ার জন্য গল্প পড়ার সময় বুকমার্ক আইকনে ক্লিক করে সংরক্ষণ করুন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "সব গল্পের তালিকায় যান",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = bookmarkedStories,
                    key = { it.id }
                ) { story ->
                    StoryCard(
                        story = story,
                        onClick = { onStoryClick(story) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarksTopBar(
    count: Int,
    onBackClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Text(
                        text = "←",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "বুকমার্ক করা গল্প",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "$count টি গল্প",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ======================= PREVIEWS =======================

private val previewBookmarks = listOf(
    Story(
        id = 1,
        title = "বৃষ্টিভেজা বিকেলে এক কাপ চা",
        category = "Romance",
        author = "অনিন্দ্য রায়",
        content = "বাইরে তখন ঝুম বৃষ্টি নামছিল...",
        isLocked = false,
        readTime = "3 min"
    ),
    Story(
        id = 3,
        title = "বন্ধ ঘরের চাবি",
        category = "Mystery",
        author = "অদিতি মজুমদার",
        content = "প্রখ্যাত চিত্রশিল্পী সমরেশ বাবুর মৃত্যুর পর...",
        isLocked = false,
        readTime = "5 min"
    )
)

@Preview(name = "Bookmarks Screen - With Items", showBackground = true)
@Composable
fun BookmarksScreenPreview() {
    ATSBookTheme {
        BookmarksScreen(
            bookmarkedStories = previewBookmarks,
            onStoryClick = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Bookmarks Screen - Empty", showBackground = true)
@Composable
fun BookmarksScreenEmptyPreview() {
    ATSBookTheme {
        BookmarksScreen(
            bookmarkedStories = emptyList(),
            onStoryClick = {},
            onBackClick = {}
        )
    }
}
