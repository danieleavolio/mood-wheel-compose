package com.example.moodwheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.moodwheel.ui.screens.ExportScreen
import com.example.moodwheel.ui.screens.ExportViewModelFactory
import com.example.moodwheel.ui.screens.HomeScreen
import com.example.moodwheel.ui.screens.HomeViewModel
import com.example.moodwheel.ui.screens.HomeViewModelFactory
import com.example.moodwheel.ui.screens.OnboardingScreen
import com.example.moodwheel.ui.screens.StatsScreen
import com.example.moodwheel.ui.theme.MoodWheelTheme

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

private enum class Screen {
    Home,
    Calendar,
    Stats,
    Export,
    Add
}

@Composable
private fun MoodApp(repository: MoodRepository) {
    var screen by remember { mutableStateOf(Screen.Home) }

    if (screen == Screen.Add) {
        AddMoodScreen(
            viewModel = viewModel(factory = AddMoodViewModelFactory(repository)),
            onClose = { screen = Screen.Home },
            onSaved = { screen = Screen.Home }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(Screen.Home, Screen.Calendar, Screen.Stats, Screen.Export).forEach { item ->
                    NavigationBarItem(
                        selected = screen == item,
                        onClick = { screen = item },
                        icon = { NavGlyph(tabIcon(item), selected = screen == item) },
                        label = { Text(tabLabel(item)) }
                    )
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = screen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screenTransition"
        ) { target ->
            when (target) {
                Screen.Home -> {
                    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                    val state by homeViewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        onAddMood = { screen = Screen.Add },
                        modifier = Modifier.padding(padding)
                    )
                }

                Screen.Calendar -> {
                    val vm: CalendarStatsViewModel = viewModel(factory = CalendarStatsViewModelFactory(repository))
                    val state by vm.calendarUiState.collectAsStateWithLifecycle()
                    CalendarScreen(
                        state = state,
                        onPreviousMonth = vm::previousMonth,
                        onNextMonth = vm::nextMonth,
                        modifier = Modifier.padding(padding)
                    )
                }

                Screen.Stats -> {
                    val vm: CalendarStatsViewModel = viewModel(factory = CalendarStatsViewModelFactory(repository))
                    val entries by vm.allEntries.collectAsStateWithLifecycle()
                    StatsScreen(entries = entries, modifier = Modifier.padding(padding))
                }

                Screen.Export -> {
                    ExportScreen(
                        viewModel = viewModel(factory = ExportViewModelFactory(repository)),
                        modifier = Modifier.padding(padding)
                    )
                }

                Screen.Add -> Unit
            }
        }
    }
}

private fun tabLabel(screen: Screen): String =
    when (screen) {
        Screen.Home -> "Home"
        Screen.Calendar -> "Calendario"
        Screen.Stats -> "Statistiche"
        Screen.Export -> "Esporta"
        Screen.Add -> ""
    }

private fun tabIcon(screen: Screen): String =
    when (screen) {
        Screen.Home -> "Home"
        Screen.Calendar -> "Cal"
        Screen.Stats -> "Trend"
        Screen.Export -> "JSON"
        Screen.Add -> ""
    }
