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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
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
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
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
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Profilo e dati", style = MaterialTheme.typography.headlineSmall)

            ProfileCard(
                profileName = profileName,
                avatarPath = avatarPath,
                onNameChange = onNameChange,
                onAvatarClick = { avatarLauncher.launch("image/*") },
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange
            )

            PrivacyCard()

            BackupCard(
                entriesCount = entries.size,
                message = message,
                onExport = { exportLauncher.launch("mood-wheel-export.json") },
                onImport = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) }
            )
        }
    }
}

@Composable
private fun ProfileCard(
    profileName: String,
    avatarPath: String?,
    onNameChange: (String) -> Unit,
    onAvatarClick: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.clickable(onClick = onAvatarClick)) {
                    ProfileAvatar(name = profileName, avatarPath = avatarPath, size = 58.dp)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(profileName.ifBlank { "Il tuo spazio" }, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Identita locale, solo sul telefono.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Tema scuro", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Controllo manuale, sempre disponibile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
            }
        }
    }
}

@Composable
private fun PrivacyCard() {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoftBadge("S")
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Privacy e sicurezza", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Nessun account, nessun cloud, nessuna sincronizzazione. I dati restano sul telefono.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BackupCard(
    entriesCount: Int,
    message: String,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    CalmCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftBadge("B")
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Backup locale", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$entriesCount momenti pronti. $message",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            GradientButton(text = "Esporta in JSON", onClick = onExport, modifier = Modifier.fillMaxWidth())
            SecondaryButton(text = "Importa da JSON", onClick = onImport, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SoftBadge(label: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
    }
}
