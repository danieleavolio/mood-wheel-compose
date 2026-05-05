package com.example.moodwheel.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moodwheel.R
import com.example.moodwheel.ui.components.AppIllustration
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.EmotionArtwork
import com.example.moodwheel.ui.components.GradientButton

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("I tuoi dati restano solo sul tuo telefono.") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportTo(context.contentResolver, uri) { success ->
                message = if (success) {
                    "Esportazione completata."
                } else {
                    "Non sono riuscito a salvare il file. Riprova con calma."
                }
            }
        }
    }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Esporta dati", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppIllustration(resId = R.drawable.export_json)
                    Text("Esporta i tuoi momenti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Scarica tutti i tuoi dati in un file JSON per conservarli o portarli altrove.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("${entries.size} momenti pronti", fontWeight = FontWeight.SemiBold)
                }
            }

            GradientButton(
                text = "Esporta in JSON",
                onClick = { launcher.launch("mood-wheel-export.json") },
                modifier = Modifier.fillMaxWidth()
            )

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmotionArtwork(emotion = null, size = 54.dp)
                    Text(
                        text = "$message Nessuna sincronizzazione. Nessun account.",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
