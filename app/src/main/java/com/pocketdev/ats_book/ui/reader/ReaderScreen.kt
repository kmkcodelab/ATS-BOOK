package com.pocketdev.ats_book.ui.reader

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.ats_book.data.model.Story
import com.pocketdev.ats_book.ui.components.AdBanner
import com.pocketdev.ats_book.ui.components.CategoryBadge
import com.pocketdev.ats_book.ui.theme.ATSBookTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    story: Story,
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    fontSizeMultiplier: Float,
    onFontSizeChange: (Float) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Calculate reading progress (0f to 1f)
    val progress = if (scrollState.maxValue > 0) {
        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    } else {
        0f
    }

    Scaffold(
        topBar = {
            ReaderTopBar(
                title = story.title,
                onBackClick = onBackClick,
                isBookmarked = isBookmarked,
                onToggleBookmark = onToggleBookmark,
                onShareClick = {
                    val shareText = "গল্প: ${story.title}\nলেখক: ${story.author}\n\n${story.content.take(200)}...\n\n- ats book অ্যাপে পুরো গল্পটি পড়ুন।"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "গল্প শেয়ার করুন")
                    context.startActivity(shareIntent)
                },
                fontSizeMultiplier = fontSizeMultiplier,
                onFontSizeChange = onFontSizeChange,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        },
        bottomBar = {
            AdBanner()
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Reading Progress Indicator at top
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Scrollable Story Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Category and Read Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = story.category)

                    Text(
                        text = "⏱ পড়তে সময় লাগবে: ${story.readTime}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = (26 * fontSizeMultiplier).sp,
                        lineHeight = (34 * fontSizeMultiplier).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Author
                Text(
                    text = "কলমে: ${story.author}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = (15 * fontSizeMultiplier).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Story Content with 1.5x line height and serif font
                Text(
                    text = story.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = (17 * fontSizeMultiplier).sp,
                        lineHeight = (28 * fontSizeMultiplier).sp // ~1.6x line height
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(40.dp))

                // End of Story Footer Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "— সমাপ্ত —",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "গল্পটি কেমন লাগল? পছন্দের তালিকায় রাখতে বুকমার্ক করুন অথবা বন্ধুদের সাথে শেয়ার করুন!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onToggleBookmark,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isBookmarked) "✓ বুকমার্ক করা আছে" else "🔖 বুকমার্কে রাখুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ReaderTopBar(
    title: String,
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    fontSizeMultiplier: Float,
    onFontSizeChange: (Float) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Back Button
                IconButton(onClick = onBackClick) {
                    Text(
                        text = "←",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            // Controls: Bookmark, Font Resizer, Dark Mode Toggle, Share Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Bookmark Toggle Button
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        text = if (isBookmarked) "★" else "☆",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Font Size Decrement
                IconButton(
                    onClick = {
                        if (fontSizeMultiplier > 0.85f) {
                            onFontSizeChange(fontSizeMultiplier - 0.15f)
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Text(
                        text = "A-",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fontSizeMultiplier <= 0.85f) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Font Size Increment
                IconButton(
                    onClick = {
                        if (fontSizeMultiplier < 1.4f) {
                            onFontSizeChange(fontSizeMultiplier + 0.15f)
                        }
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Text(
                        text = "A+",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (fontSizeMultiplier >= 1.4f) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Dark Mode Toggle
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier.size(34.dp)
                ) {
                    Text(
                        text = if (isDarkMode) "☀️" else "🌙",
                        fontSize = 15.sp
                    )
                }

                // Share Button
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Text(
                        text = "↗",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ======================= PREVIEWS =======================

private val previewReaderStory = Story(
    id = 1,
    title = "বৃষ্টিভেজা বিকেলে এক কাপ চা",
    category = "Romance",
    author = "অনিন্দ্য রায়",
    content = "বাইরে তখন ঝুম বৃষ্টি নামছিল। জানালার কাঁচ বেয়ে গড়িয়ে পড়া জলের ফোঁটাগুলো যেন কোনো পুরোনো স্মৃতি বয়ে নিয়ে আসছিল। রাহুল চায়ের কাপ হাতে দাঁড়িয়ে ছিল বারান্দায়। ঠিক তখনই দরজায় মৃদু কড়া নাড়ার শব্দ। দরজা খুলতেই সামনে ভেজা শাড়িতে দাঁড়িয়ে থাকা মৌমিতা। মৌমিতার চোখের ভেজা চাহনি আর ঠোঁটের কোণে মৃদু হাসি রাহুলের সব দ্বিধা দূর করে দিল। মৌমিতা মৃদু কণ্ঠে বলল, 'খুব একা একা চা খাওয়া হচ্ছে? আমাকে কি এক কাপ দেয়া যাবে?' রাহুল নিঃশব্দে হেসে মৌমিতার হাত ধরে ভেতরে টেনে নিল। সেই বিকেলে বৃষ্টির প্রতিটি ফোঁটার সাথে তাদের দীর্ঘদিনের মান-অভিমান আর না বলা কথাগুলো যেন ভালোবাসার এক নতুন সুরে বাঁধা পড়ল।",
    isLocked = false,
    readTime = "3 min"
)

@Preview(name = "Reader Screen - Light Mode", showBackground = true)
@Composable
fun ReaderScreenPreview() {
    ATSBookTheme(darkTheme = false) {
        ReaderScreen(
            story = previewReaderStory,
            onBackClick = {},
            isBookmarked = true,
            onToggleBookmark = {},
            fontSizeMultiplier = 1.0f,
            onFontSizeChange = {},
            isDarkMode = false,
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    name = "Reader Screen - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ReaderScreenDarkPreview() {
    ATSBookTheme(darkTheme = true) {
        ReaderScreen(
            story = previewReaderStory,
            onBackClick = {},
            isBookmarked = false,
            onToggleBookmark = {},
            fontSizeMultiplier = 1.0f,
            onFontSizeChange = {},
            isDarkMode = true,
            onToggleDarkMode = {}
        )
    }
}
