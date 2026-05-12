package com.example.moodwheel

import android.app.Activity
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.ui.components.NavGlyph
import com.example.moodwheel.reminder.ReminderScheduler
import com.example.moodwheel.ui.screens.AddMoodScreen
import com.example.moodwheel.ui.screens.AddMoodViewModelFactory
import com.example.moodwheel.ui.screens.CalendarScreen
import com.example.moodwheel.ui.screens.CalendarStatsViewModel
import com.example.moodwheel.ui.screens.CalendarStatsViewModelFactory
import com.example.moodwheel.ui.screens.DiaryScreen
import com.example.moodwheel.ui.screens.EntryDetailScreen
import com.example.moodwheel.ui.screens.ExportScreen
import com.example.moodwheel.ui.screens.ExportViewModelFactory
import com.example.moodwheel.ui.screens.HomeScreen
import com.example.moodwheel.ui.screens.HomeViewModel
import com.example.moodwheel.ui.screens.HomeViewModelFactory
import com.example.moodwheel.ui.screens.OnboardingScreen
import com.example.moodwheel.ui.screens.StatsScreen
import com.example.moodwheel.ui.theme.MoodWheelTheme
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReminderScheduler.setup(this)
        val repository = (application as MoodWheelApplication).container.repository
        val prefs = getSharedPreferences("mood_wheel_prefs", MODE_PRIVATE)
        setContent {
            var onboardingDone by remember {
                mutableStateOf(prefs.getBoolean("onboarding_done", false))
            }
            var themeMode by remember {
                mutableStateOf(prefs.getString("theme_mode", "light") ?: "light")
            }
            val darkTheme = onboardingDone && themeMode == "dark"

            MoodWheelTheme(darkTheme = darkTheme) {
                if (onboardingDone) {
                    MoodApp(
                        repository = repository,
                        prefs = prefs,
                        darkTheme = darkTheme,
                        onDarkThemeChange = { enabled ->
                            themeMode = if (enabled) "dark" else "light"
                            prefs.edit().putString("theme_mode", themeMode).apply()
                        }
                    )
                } else {
                    OnboardingScreen(
                        onDone = {
                            prefs.edit().putBoolean("onboarding_done", true).apply()
                            onboardingDone = true
                        }
                    )
                }
            }
        }
    }
}

private enum class AppMode {
    Main,
    Add,
    Detail
}

private enum class Tab {
    Home,
    Calendar,
    Stats,
    Diary,
    Export
}

private data class LocalProfile(
    val name: String,
    val avatarPath: String?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MoodApp(
    repository: MoodRepository,
    prefs: SharedPreferences,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    var mode by remember { mutableStateOf(AppMode.Main) }
    var addInitialDate by remember { mutableStateOf<LocalDate?>(null) }
    var addSession by remember { mutableIntStateOf(0) }
    var selectedEntryId by remember { mutableStateOf<Long?>(null) }
    var profile by remember {
        mutableStateOf(
            LocalProfile(
                name = prefs.getString("profile_name", "Daniele") ?: "Daniele",
                avatarPath = prefs.getString("profile_avatar_path", null)
            )
        )
    }

    val tabs = listOf(Tab.Home, Tab.Calendar, Tab.Stats, Tab.Diary, Tab.Export)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedTab = tabs.getOrElse(pagerState.currentPage) { Tab.Home }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var lastBackPress by remember { mutableLongStateOf(0L) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val calendarVm: CalendarStatsViewModel = viewModel(factory = CalendarStatsViewModelFactory(repository))
    val entries by calendarVm.allEntries.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    BackHandler(enabled = mode == AppMode.Main && selectedTab != Tab.Home) {
        scope.launch { pagerState.animateScrollToPage(0) }
    }

    BackHandler(enabled = mode == AppMode.Main && selectedTab == Tab.Home) {
        val now = System.currentTimeMillis()
        if (now - lastBackPress < 1800) {
            (context as? Activity)?.finish()
        } else {
            lastBackPress = now
            Toast.makeText(context, "Premi ancora per uscire", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAdd(date: LocalDate? = null) {
        addInitialDate = date
        addSession += 1
        mode = AppMode.Add
    }

    fun updateName(name: String) {
        val clean = name.take(24)
        profile = profile.copy(name = clean)
        prefs.edit().putString("profile_name", clean).apply()
    }

    fun updateAvatar(path: String) {
        profile = profile.copy(avatarPath = path)
        prefs.edit().putString("profile_avatar_path", path).apply()
    }

    when (mode) {
        AppMode.Add -> {
            AddMoodScreen(
                viewModel = viewModel(
                    key = "add-$addSession",
                    factory = AddMoodViewModelFactory(repository, initialDate = addInitialDate)
                ),
                onClose = { mode = AppMode.Main },
                onSaved = { mode = AppMode.Main }
            )
            return
        }

        AppMode.Detail -> {
            val entry = entries.firstOrNull { it.id == selectedEntryId }
            if (entry == null) {
                mode = AppMode.Main
            } else {
                BackHandler { mode = AppMode.Main }
                EntryDetailScreen(
                    entry = entry,
                    onBack = { mode = AppMode.Main },
                    onSave = calendarVm::updateEntry,
                    onDelete = calendarVm::deleteEntry
                )
                return
            }
        }

        AppMode.Main -> Unit
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                tabs.forEach { item ->
                    val selected = selectedTab == item
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(tabs.indexOf(item))
                            }
                        },
                        alwaysShowLabel = false,
                        icon = { NavGlyph(tabIcon(item), selected = selected) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f) else Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            beyondViewportPageCount = 0,
            key = { page -> tabs[page] }
        ) { page ->
            when (tabs[page]) {
                Tab.Home -> {
                    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                    val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        profileName = profile.name,
                        avatarPath = profile.avatarPath,
                        onAddMood = { openAdd() },
                        onDayClick = { date -> openAdd(date) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Tab.Calendar -> {
                    val state by calendarVm.calendarUiState.collectAsStateWithLifecycle()
                    CalendarScreen(
                        state = state,
                        onPreviousMonth = calendarVm::previousMonth,
                        onNextMonth = calendarVm::nextMonth,
                        onEntryClick = { entry ->
                            selectedEntryId = entry.id
                            mode = AppMode.Detail
                        },
                        onAddMoodForDate = { date -> openAdd(date) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Tab.Stats -> {
                    StatsScreen(entries = entries, modifier = Modifier.fillMaxSize())
                }

                Tab.Diary -> {
                    DiaryScreen(
                        entries = entries,
                        onEntryClick = { entry ->
                            selectedEntryId = entry.id
                            mode = AppMode.Detail
                        },
                        onAddMood = { openAdd() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Tab.Export -> {
                    ExportScreen(
                        viewModel = viewModel(factory = ExportViewModelFactory(repository)),
                        profileName = profile.name,
                        avatarPath = profile.avatarPath,
                        onNameChange = ::updateName,
                        onAvatarChange = ::updateAvatar,
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun tabIcon(tab: Tab): String =
    when (tab) {
        Tab.Home -> "home"
        Tab.Calendar -> "calendar"
        Tab.Stats -> "stats"
        Tab.Diary -> "diary"
        Tab.Export -> "export"
    }
