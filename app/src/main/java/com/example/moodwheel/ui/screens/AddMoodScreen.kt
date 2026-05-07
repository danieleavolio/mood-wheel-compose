package com.example.moodwheel.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.moodwheel.ui.components.EmotionWheel
import com.example.moodwheel.ui.components.GradientButton
import com.example.moodwheel.ui.components.MoodListSelector
import com.example.moodwheel.ui.components.SecondaryButton
import com.example.moodwheel.ui.components.SoftTimePickerSheet
import com.example.moodwheel.ui.components.StepProgress
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

    BackHandler {
        if (state.step > 1) {
            viewModel.previousStep()
        } else {
            onClose()
        }
    }

    CalmBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClose) { Text("X") }
                Text(
                    text = "Aggiungi umore",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Text("${state.step} di 4", style = MaterialTheme.typography.labelLarge)
            }

            StepProgress(currentStep = state.step, totalSteps = 4)

            AnimatedContent(
                targetState = state.step,
                modifier = Modifier.weight(1f),
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.step > 1) {
                    SecondaryButton(
                        text = "Indietro",
                        onClick = viewModel::previousStep,
                        modifier = Modifier.weight(1f)
                    )
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Come ti senti?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Scegli il livello che rappresenta meglio il tuo stato d'animo.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        MoodListSelector(selected = state.moodLevel, onSelect = onSelect)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun EmotionStep(
    state: AddMoodUiState,
    viewModel: AddMoodViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Quale emozione prevale?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Seleziona una categoria dalla ruota.", style = MaterialTheme.typography.bodyMedium)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = selected.softColor()),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            Text("Scegli le emozioni che senti.")
                        }
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selected.microEmotions.take(10).forEach { label ->
                            val isSelected = label in state.selectedMicro
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleMicro(label) },
                                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = selected.color(),
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = selected.color().copy(alpha = 0.16f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
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
    var showTimeSheet by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Quando e successo?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Puoi lasciare l'ora attuale o cambiarla.", textAlign = TextAlign.Center)

        Button(
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
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(state.date.formatDate())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeTile(value = "%02d".format(state.time.hour), onClick = { showTimeSheet = true })
            Text(":", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.headlineLarge)
            TimeTile(value = "%02d".format(state.time.minute), onClick = { showTimeSheet = true })
        }
        Text("Tocca l'ora se vuoi cambiarla.", style = MaterialTheme.typography.bodySmall)
    }

    if (showTimeSheet) {
        SoftTimePickerSheet(
            initialTime = state.time,
            onDismiss = { showTimeSheet = false },
            onConfirm = { time ->
                viewModel.updateTime(time)
                showTimeSheet = false
            }
        )
    }
}

@Composable
private fun TimeTile(
    value: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.height(88.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.46f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Vuoi aggiungere una nota?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Racconta cosa e successo, solo se ti va.", textAlign = TextAlign.Center)
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
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = macro.softColor(),
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.38f)
                    ),
                    supportingText = {
                        Text("${state.note.length}/300")
                    }
                )
            }
        }
    }
}
