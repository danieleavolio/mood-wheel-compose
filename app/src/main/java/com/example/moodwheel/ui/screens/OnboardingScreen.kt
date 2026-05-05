package com.example.moodwheel.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodwheel.R
import com.example.moodwheel.ui.components.AppIllustration
import com.example.moodwheel.ui.components.CalmBackground
import com.example.moodwheel.ui.components.CalmCard
import com.example.moodwheel.ui.components.GradientButton

private data class OnboardingPage(
    val title: String,
    val body: String,
    val imageRes: Int
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Un check-in gentile",
        body = "In meno di 30 secondi puoi segnare come stai, senza giudizio e senza obiettivi da inseguire.",
        imageRes = R.drawable.onboarding_privacy
    ),
    OnboardingPage(
        title = "Le parole aiutano",
        body = "Parti da una categoria grande, poi scegli parole piu precise solo se ti va.",
        imageRes = R.drawable.onboarding_wheel
    ),
    OnboardingPage(
        title = "Solo sul tuo telefono",
        body = "Nessun account. Nessuna sincronizzazione. Puoi esportare tutto in JSON quando vuoi.",
        imageRes = R.drawable.export_json
    )
)

@Composable
fun OnboardingScreen(
    onDone: () -> Unit
) {
    var page by remember { mutableIntStateOf(0) }
    val current = onboardingPages[page]

    CalmBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            AnimatedContent(
                targetState = current,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboardingPage"
            ) { item ->
                CalmCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        AppIllustration(resId = item.imageRes)
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = item.body,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onboardingPages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == page) 22.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (index == page) MaterialTheme.colorScheme.primary else Color(0xFFD8D4E5))
                        )
                    }
                }

                GradientButton(
                    text = if (page == onboardingPages.lastIndex) "Inizia" else "Continua",
                    onClick = {
                        if (page == onboardingPages.lastIndex) onDone() else page += 1
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(
                    visible = page < onboardingPages.lastIndex,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    OutlinedButton(onClick = onDone) {
                        Text("Salta")
                    }
                }
            }
        }
    }
}
