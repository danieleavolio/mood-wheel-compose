package com.example.moodwheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodwheel.data.repository.MoodRepository
import com.example.moodwheel.ui.components.NavGlyph
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
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as MoodWheelApplication).container.repository
        val prefs = getSharedPreferences("mood_wheel_prefs", MODE_PRIVATE)
        setContent {
            MoodWheelTheme {
                var onboardingDone by remember {
                    mutableStateOf(prefs.getBoolean("onboarding_done", false))
                }

                if (onboardingDone) {
                    MoodApp(repository)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MoodApp(repository: MoodRepository) {
    var mode by remember { mutableStateOf(AppMode.Main) }
    var addInitialDate by remember { mutableStateOf<LocalDate?>(null) }
    var addSession by remember { mutableIntStateOf(0) }
    var selectedEntryId by remember { mutableStateOf<Long?>(null) }

    val tabs = listOf(Tab.Home, Tab.Calendar, Tab.Stats, Tab.Diary, Tab.Export)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    val calendarVm: CalendarStatsViewModel = viewModel(factory = CalendarStatsViewModelFactory(repository))
    val entries by calendarVm.allEntries.collectAsStateWithLifecycle()

    fun openAdd(date: LocalDate? = null) {
        addInitialDate = date
        addSession += 1
        mode = AppMode.Add
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
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    val selected = pagerState.currentPage == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = { NavGlyph(tabIcon(item), selected = selected) },
                        label = { Text(tabLabel(item)) }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier.padding(padding)
        ) { page ->
            when (tabs[page]) {
                Tab.Home -> {
                    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                    val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        onAddMood = { openAdd() }
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
                        onAddMoodForDate = { date -> openAdd(date) }
                    )
                }

                Tab.Stats -> {
                    StatsScreen(entries = entries)
                }

                Tab.Diary -> {
                    DiaryScreen(
                        entries = entries,
                        onEntryClick = { entry ->
                            selectedEntryId = entry.id
                            mode = AppMode.Detail
                        },
                        onAddMood = { openAdd() }
                    )
                }

                Tab.Export -> {
                    ExportScreen(
                        viewModel = viewModel(factory = ExportViewModelFactory(repository))
                    )
                }
            }
        }
    }
}

private fun tabLabel(tab: Tab): String =
    when (tab) {
        Tab.Home -> "Home"
        Tab.Calendar -> "Calendario"
        Tab.Stats -> "Statistiche"
        Tab.Diary -> "Diario"
        Tab.Export -> "Esporta"
    }

private fun tabIcon(tab: Tab): String =
    when (tab) {
        Tab.Home -> "Home"
        Tab.Calendar -> "Cal"
        Tab.Stats -> "Trend"
        Tab.Diary -> "Diario"
        Tab.Export -> "JSON"
    }
