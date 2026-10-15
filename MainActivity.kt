package com.socialnews.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF101D35)
private val Orange = Color(0xFFFF7A1A)
private val Canvas = Color(0xFFF4F6FA)
private val Ink = Color(0xFF172033)

data class Story(val category: String, val title: String, val summary: String, val source: String)

private val demoStories = listOf(
    Story("India", "Demo story: connect a verified news source", "This is sample content for development, not current news. Configure an approved API or RSS feed before launch.", "DEMO • Not live"),
    Story("Education", "Education and exam updates", "Connect official education boards and government portals to show verified notices with source links.", "DEMO • Not live"),
    Story("Technology", "Technology and science briefing", "Use a licensed news API or publisher RSS feed and preserve attribution to the original publisher.", "DEMO • Not live"),
    Story("Sports", "Sports updates", "Connect a legitimate sports data provider to display scores and schedules.", "DEMO • Not live"),
    Story("Government Jobs", "Government job notifications", "Link to official recruitment notices. Always verify dates, eligibility and application links.", "DEMO • Not live"),
    Story("Agriculture", "Agriculture and business", "Connect trusted sources for market, weather and agriculture information.", "DEMO • Not live")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SocialNewsApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialNewsApp() {
    var selectedTab by remember { mutableStateOf("Home") }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var darkMode by remember { mutableStateOf(false) }
    var english by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(setOf<String>()) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    val scheme = if (darkMode) darkColorScheme(primary = Orange, background = Color(0xFF0C1424), surface = Color(0xFF17243A))
                 else lightColorScheme(primary = Orange, background = Canvas, surface = Color.White)
    MaterialTheme(colorScheme = scheme) {
        if (selectedStory != null) {
            StoryDetail(selectedStory!!, saved.contains(selectedStory!!.title),
                onBack = { selectedStory = null },
                onSave = { isSaved ->
                    saved = if (isSaved) saved + selectedStory!!.title else saved - selectedStory!!.title
                })
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(Modifier.size(38.dp).background(Orange, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Podcasts, null, tint = Color.White)
                                }
                                Column {
                                    Text("Social News", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text(if (english) "Your daily information hub" else "आपकी रोज़ की जानकारी", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { darkMode = !darkMode }) { Icon(if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "Toggle theme") }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        listOf("Home", "News", "Live", "Discover", "Profile").forEach { tab ->
                            val icon = when (tab) {
                                "Home" -> Icons.Default.Home
                                "News" -> Icons.Default.Article
                                "Live" -> Icons.Default.PlayCircle
                                "Discover" -> Icons.Default.Explore
                                else -> Icons.Default.Person
                            }
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                icon = { Icon(icon, contentDescription = tab) },
                                label = { Text(if (english) tab else when(tab) { "Home"->"होम"; "News"->"न्यूज़"; "Live"->"लाइव"; "Discover"->"खोजें"; else->"प्रोफ़ाइल" }) }
                            )
                        }
                    }
                }
            ) { padding ->
                when (selectedTab) {
                    "Live" -> LiveScreen(Modifier.padding(padding))
                    "Profile" -> ProfileScreen(Modifier.padding(padding), darkMode, english,
                        onDark = { darkMode = it }, onLanguage = { english = it }, savedCount = saved.size)
                    "Discover" -> DiscoverScreen(Modifier.padding(padding), onChoose = { selectedCategory = it; selectedTab = "News" })
                    else -> NewsHome(
                        modifier = Modifier.padding(padding),
                        english = english,
                        query = query,
                        onQuery = { query = it },
                        selectedCategory = selectedCategory,
                        onCategory = { selectedCategory = it },
                        saved = saved,
                        onStory = { selectedStory = it },
                        onSave = { story -> saved = if (saved.contains(story.title)) saved - story.title else saved + story.title },
                        onlySaved = selectedTab == "News" && selectedCategory == "Saved"
                    )
                }
            }
        }
    }
}

@Composable
fun NewsHome(
    modifier: Modifier, english: Boolean, query: String, onQuery: (String) -> Unit,
    selectedCategory: String, onCategory: (String) -> Unit, saved: Set<String>,
    onStory: (Story) -> Unit, onSave: (Story) -> Unit, onlySaved: Boolean
) {
    val categories = listOf("All", "India", "World", "Local", "Sports", "Education", "Government Jobs", "Technology", "Saved")
    val filtered = demoStories.filter {
        (selectedCategory == "All" || selectedCategory == "Saved" || it.category.equals(selectedCategory, true)) &&
        (it.title.contains(query, true) || it.summary.contains(query, true)) &&
        (!onlySaved || saved.contains(it.title)) &&
        (selectedCategory != "Saved" || saved.contains(it.title))
    }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)) {
        item {
            OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (english) "Search news, jobs, exams…" else "न्यूज़, नौकरी, परीक्षा खोजें…") },
                leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, null, tint = Orange)
                        Spacer(Modifier.width(8.dp))
                        Text(if (english) "BREAKING NEWS" else "ब्रेकिंग न्यूज़", color = Orange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(Modifier.weight(1f))
                        Text("DEMO", color = Color.White.copy(alpha = .7f), fontSize = 10.sp)
                    }
                    Text(if (english) "Your trusted daily briefing starts here" else "आपकी भरोसेमंद दैनिक जानकारी यहाँ से शुरू", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(if (english) "Sample banner only — no live news source is configured." else "यह केवल नमूना बैनर है — अभी लाइव न्यूज़ स्रोत नहीं जुड़ा है।", color = Color.White.copy(alpha = .78f), fontSize = 12.sp)
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (english) "Browse topics" else "कैटेगरी चुनें", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Text(if (english) "Demo feed" else "डेमो फ़ीड", color = Orange, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(selected = selectedCategory == category, onClick = { onCategory(category) }, label = { Text(category) })
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (english) "Latest updates" else "ताज़ा अपडेट", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.AutoAwesome, null, tint = Orange)
            }
        }
        if (filtered.isEmpty()) {
            item {
                EmptyCard(if (english) "No matching stories" else "कोई खबर नहीं मिली",
                    if (english) "Try another search or category. Real stories appear after configuring a source." else "दूसरी खोज या कैटेगरी आज़माएँ। असली खबरें स्रोत जोड़ने के बाद दिखेंगी।")
            }
        } else {
            items(filtered) { story ->
                StoryCard(story, saved.contains(story.title), onClick = { onStory(story) }, onSave = { onSave(story) })
            }
        }
        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Quiz, null, tint = Orange, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (english) "Daily GK Quiz" else "आज का GK क्विज़", fontWeight = FontWeight.Bold)
                        Text(if (english) "Quiz module can be added next" else "क्विज़ मॉड्यूल अगले चरण में जोड़ा जा सकता है", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("→", color = Orange, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun StoryCard(story: Story, isSaved: Boolean, onClick: () -> Unit, onSave: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Orange.copy(alpha = .13f), shape = RoundedCornerShape(8.dp)) {
                    Text(story.category, Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = Orange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onSave) { Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, "Save story", tint = Orange) }
            }
            Text(story.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.clickable(onClick = onClick))
            Spacer(Modifier.height(6.dp))
            Text(story.summary, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.clickable(onClick = onClick))
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Orange, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Text(story.source, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onClick) { Text("Read details") }
            }
        }
    }
}

@Composable
fun StoryDetail(story: Story, saved: Boolean, onBack: () -> Unit, onSave: (Boolean) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Story details") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            Text(story.category.uppercase(), color = Orange, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(story.title, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Text(story.source, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            Text(story.summary, fontSize = 16.sp, lineHeight = 25.sp)
            Spacer(Modifier.height(20.dp))
            Text("Development demo: publisher attribution, original article URL and verified article body must come from a configured content source.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onSave(!saved) }) { Icon(if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null); Spacer(Modifier.width(8.dp)); Text(if (saved) "Remove bookmark" else "Save article") }
        }
    }
}

@Composable
fun LiveScreen(modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Live Now", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Card(shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.LiveTv, null, tint = Orange, modifier = Modifier.size(58.dp))
                Text("No live streams configured", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Add an authorized HLS or other supported stream URL in the app's data-source configuration. The app will not claim a channel is live without a verified source.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text("To enable playback: integrate AndroidX Media3 ExoPlayer, verify stream status, and test only with a stream you are authorized to use.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DiscoverScreen(modifier: Modifier, onChoose: (String) -> Unit) {
    val topics = listOf("India", "World", "Local", "Politics", "Business", "Technology", "Sports", "Entertainment", "Education", "Government Jobs", "Government Schemes", "Agriculture", "Health", "General Knowledge")
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Discover", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text("Choose a topic to filter the demo feed.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        topics.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { topic ->
                    Card(Modifier.weight(1f).padding(bottom = 10.dp).clickable { onChoose(topic) }, shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Feed, null, tint = Orange)
                            Spacer(Modifier.width(8.dp))
                            Text(topic, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier, darkMode: Boolean, english: Boolean, onDark: (Boolean) -> Unit, onLanguage: (Boolean) -> Unit, savedCount: Int) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(if (english) "Profile & Settings" else "प्रोफ़ाइल और सेटिंग्स", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Card(shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(if (english) "Preferences" else "पसंद", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, null, tint = Orange)
                    Spacer(Modifier.width(10.dp))
                    Text(if (english) "Dark mode" else "डार्क मोड", modifier = Modifier.weight(1f))
                    Switch(checked = darkMode, onCheckedChange = onDark)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null, tint = Orange)
                    Spacer(Modifier.width(10.dp))
                    Text(if (english) "English language" else "अंग्रेज़ी भाषा", modifier = Modifier.weight(1f))
                    Switch(checked = english, onCheckedChange = onLanguage)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bookmarks, null, tint = Orange)
                    Spacer(Modifier.width(10.dp))
                    Text(if (english) "Saved articles" else "सेव किए लेख", modifier = Modifier.weight(1f))
                    Text("$savedCount", color = Orange, fontWeight = FontWeight.Bold)
                }
            }
        }
        Card(shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(if (english) "About & safety" else "ऐप की जानकारी", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Social News • MVP demo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("No real news, API, notifications, ads, or live stream is configured in this starter project. Add a privacy policy and terms before public release.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun EmptyCard(title: String, body: String) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.SearchOff, null, tint = Orange, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
