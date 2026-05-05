package com.example.moodwheel.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moodwheel.domain.model.EmotionCatalog
import com.example.moodwheel.domain.model.MoodLevel
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.components.EmotionChips
import com.example.moodwheel.ui.components.EmotionWheel
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.components.MoodSelector
import com.example.moodwheel.ui.theme.color
import com.example.moodwheel.ui.theme.softColor
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AddMoodScreen(
    viewModel: AddMoodViewModel,
    onClose: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    CalmBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onClose) { Text("Chiudi") }
                Text(
                    text = "Aggiungi umore",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Text("${state.step} di 4", style = MaterialTheme.typography.labelLarge)
            }

            LinearProgressIndicator(
                progress = { state.step / 4f },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = state.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "addMoodStep"
            ) { step ->
                when (step) {
                    1 -> MoodStep(state = state, onSelect = viewModel::selectMood)
                    2 -> EmotionStep(state = state, viewModel = viewModel)
                    3 -> DateTimeStep(state = state, viewModel = viewModel)
                    4 -> NoteStep(state = state, onNote = viewModel::updateNote)
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.step > 1) {
                    OutlinedButton(
                        onClick = viewModel::previousStep,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Indietro")
                    }
                }
                GradientButton(
                    text = if (state.step == 4) "Salva" else "Avanti",
                    onClick = {
                        if (state.step == 4) {
                            viewModel.save(onSaved)
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    enabled = state.canGoNext && !state.isSaving,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MoodStep(
    state: AddMoodUiState,
    onSelect: (MoodLevel) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Come ti senti in generale?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Scegli il livello che rappresenta meglio il tuo stato d'animo.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        CalmCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                MoodSelector(selected = state.moodLevel, onSelect = onSelect)
            }
        }
    }
}

@Composable
private fun EmotionStep(
    state: AddMoodUiState,
    viewModel: AddMoodViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Quale emozione prevale?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Tocca una categoria grande. Le parole arrivano dopo.", style = MaterialTheme.typography.bodyMedium)
        EmotionWheel(
            selected = state.selectedMacro,
            onSelect = viewModel::selectMacro
        )

        val selected = state.selectedMacro
        if (selected == null) {
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Non serve essere precisi subito. Scegli quello che assomiglia di piu al momento.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(selected.softColor())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmotionArtwork(emotion = selected, size = 58.dp)
                        Column {
                            Text(selected.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Puoi selezionarne piu di una.")
                        }
                    }
                    EmotionChips(
                        emotion = selected,
                        selected = state.selectedMicro,
                        onToggle = viewModel::toggleMicro
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTimeStep(
    state: AddMoodUiState,
    viewModel: AddMoodViewModel
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Quando e successo?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Puoi lasciare l'ora attuale o cambiarla.", textAlign = TextAlign.Center)

        CalmCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                viewModel.updateDate(LocalDate.of(year, month + 1, day))
                            },
                            state.date.year,
                            state.date.monthValue - 1,
                            state.date.dayOfMonth
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(state.date.formatDate())
                }

                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                viewModel.updateTime(LocalTime.of(hour, minute))
                            },
                            state.time.hour,
                            state.time.minute,
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("%02d:%02d".format(state.time.hour, state.time.minute))
                }
            }
        }
    }
}

@Composable
private fun NoteStep(
    state: AddMoodUiState,
    onNote: (String) -> Unit
) {
    val macro = state.selectedMacro ?: EmotionCatalog.emotions.first()
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Vuoi aggiungere una nota?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Racconta cosa e successo, solo se ti va.")
        CalmCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmotionArtwork(emotion = macro, size = 54.dp)
                    Text(
                        text = "Categoria scelta: ${macro.label}",
                        color = macro.color(),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNote,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    placeholder = { Text("Una frase, un pensiero, o anche niente.") },
                    minLines = 6,
                    supportingText = {
                        Text("${state.note.length}/300")
                    }
                )
            }
        }
    }
}
