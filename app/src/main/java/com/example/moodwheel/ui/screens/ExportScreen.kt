package com.example.moodwheel.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.moodwheel.ui.components.ProfileAvatar
import com.example.moodwheel.ui.components.SecondaryButton

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    profileName: String,
    avatarPath: String?,
    onNameChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("Tutto resta sul tuo telefono.") }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportTo(context.contentResolver, uri) { success ->
                message = if (success) {
                    "Export completato. Hai una copia JSON dei tuoi momenti."
                } else {
                    "Non sono riuscito a salvare il file. Riprova con calma."
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importFrom(context.contentResolver, uri) { result ->
                message = if (result.success) {
                    "Import completato: ${result.imported} nuovi, ${result.skipped} gia presenti."
                } else {
                    "Non sono riuscito a leggere quel JSON."
                }
            }
        }
    }

    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.saveAvatar(context, uri) { path ->
                if (path != null) {
                    onAvatarChange(path)
                    message = "Avatar aggiornato."
                } else {
                    message = "Non sono riuscito a salvare l'avatar."
                }
            }
        }
    }

    CalmBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Profilo e dati", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            ProfileCard(
                profileName = profileName,
                avatarPath = avatarPath,
                onNameChange = onNameChange,
                onAvatarClick = { avatarLauncher.launch("image/*") }
            )

            DataHero(entriesCount = entries.size)

            GradientButton(
                text = "Esporta in JSON",
                onClick = { exportLauncher.launch("mood-wheel-export.json") },
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = "Importa da JSON",
                onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
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

@Composable
private fun ProfileCard(
    profileName: String,
    avatarPath: String?,
    onNameChange: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(modifier = Modifier.clickable(onClick = onAvatarClick)) {
                    ProfileAvatar(name = profileName, avatarPath = avatarPath, size = 72.dp)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Il tuo spazio", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Nome e avatar restano locali.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedTextField(
                value = profileName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Come vuoi essere salutato?") },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF8F4FF),
                    unfocusedContainerColor = Color(0xFFF8F4FF)
                )
            )

            Text(
                "Tocca l'avatar per scegliere una foto.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DataHero(entriesCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFECE7FF), Color(0xFFFFF3DF), Color(0xFFF7FBFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppIllustration(resId = R.drawable.export_json)
            Text("Backup locale", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Esporta o ricarica i tuoi momenti quando cambi telefono o vuoi tenere una copia.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PrivacyBadge("$entriesCount momenti pronti")
        }
    }
}

@Composable
private fun PrivacyBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White.copy(alpha = 0.76f))
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
}
