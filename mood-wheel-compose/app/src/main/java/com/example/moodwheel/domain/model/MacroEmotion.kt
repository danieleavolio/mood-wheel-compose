package com.example.moodwheel.domain.model

data class MacroEmotion(
    val id: String,
    val label: String,
    val colorHex: Long,
    val microEmotions: List<String>
)

object EmotionCatalog {
    val emotions = listOf(
        MacroEmotion(
            id = "happiness",
            label = "Felicità",
            colorHex = 0xFF6CC35B,
            microEmotions = listOf("sereno", "gioioso", "fiducioso", "grato", "tranquillo")
        ),
        MacroEmotion(
            id = "sadness",
            label = "Tristezza",
            colorHex = 0xFF78AEEB,
            microEmotions = listOf("solo", "depresso", "stanco", "vulnerabile", "malinconico")
        ),
        MacroEmotion(
            id = "anger",
            label = "Rabbia",
            colorHex = 0xFFFF7468,
            microEmotions = listOf("frustrato", "irritato", "aggressivo", "teso", "infastidito")
        ),
        MacroEmotion(
            id = "fear",
            label = "Paura",
            colorHex = 0xFFA682E8,
            microEmotions = listOf("ansioso", "insicuro", "preoccupato", "spaventato", "sospeso")
        ),
        MacroEmotion(
            id = "disgust",
            label = "Disgusto",
            colorHex = 0xFFFFA14D,
            microEmotions = listOf("deluso", "respinto", "a disagio", "scettico", "distante")
        ),
        MacroEmotion(
            id = "surprise",
            label = "Sorpresa",
            colorHex = 0xFFFFD45A,
            microEmotions = listOf("sorpreso", "curioso", "confuso", "meravigliato", "spiazzato")
        )
    )

    fun byId(id: String): MacroEmotion =
        emotions.firstOrNull { it.id == id } ?: emotions.first()
}
