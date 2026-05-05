package com.example.moodwheel.domain.model

data class EmotionGroup(
    val label: String,
    val children: List<String>
)

data class MacroEmotion(
    val id: String,
    val label: String,
    val colorHex: Long,
    val groups: List<EmotionGroup>
) {
    val microEmotions: List<String> =
        groups.flatMap { group -> listOf(group.label) + group.children }.distinct()
}

object EmotionCatalog {
    val emotions = listOf(
        MacroEmotion(
            id = "happiness",
            label = "Felicità",
            colorHex = 0xFF6CC35B,
            groups = listOf(
                group("Accettato", "Soddisfatto", "Rispettato"),
                group("Interessato", "Divertito", "Curioso"),
                group("Infimo", "Giocoso", "Delicato"),
                group("Gioioso", "Estasiato", "Liberato"),
                group("Ottimista", "Ispirato", "Aperto"),
                group("Tranquillo", "Speranzoso", "Amorevole"),
                group("Potente", "Coraggioso", "Provocatorio"),
                group("Orgoglioso", "Fiducioso", "Importante")
            )
        ),
        MacroEmotion(
            id = "sadness",
            label = "Tristezza",
            colorHex = 0xFF78AEEB,
            groups = listOf(
                group("Abbandonato", "Ignorato", "Vittimizzato"),
                group("Annoiato", "Apatico", "Indifferente"),
                group("Depresso", "Vuoto", "Inferiore"),
                group("Disperato", "Impotente", "Vulnerabile"),
                group("Colpevole", "Disonorevole", "Pentito"),
                group("Solo", "Abbandonato", "Isolato")
            )
        ),
        MacroEmotion(
            id = "anger",
            label = "Rabbia",
            colorHex = 0xFFFF7468,
            groups = listOf(
                group("Aggressivo", "Ostile", "Provocato"),
                group("Critico", "Sarcastico", "Scettico"),
                group("Distaccato", "Sospettoso", "Asociale"),
                group("Frustrato", "Infuriato", "Irritato"),
                group("Detestabile", "Rancoroso", "Violato"),
                group("Ferito", "Devastato", "Imbarazzato"),
                group("Arrabbiato", "Imbestialito", "Furioso"),
                group("Minacciato", "Insicuro", "Geloso")
            )
        ),
        MacroEmotion(
            id = "fear",
            label = "Paura",
            colorHex = 0xFFA682E8,
            groups = listOf(
                group("Ansioso", "Sopraffatto", "Preoccupato"),
                group("Umiliato", "Irrispettato", "Ridicolizzato"),
                group("Insicuro", "Inadeguato", "Inferiore"),
                group("Respinto", "Alienato", "Inadeguato"),
                group("Impaurito", "Spaventato", "Terrorizzato"),
                group("Sottomesso", "Insignificante", "Indegno")
            )
        ),
        MacroEmotion(
            id = "disgust",
            label = "Disgusto",
            colorHex = 0xFFFFA14D,
            groups = listOf(
                group("Sfuggievole", "Avversione", "Esitante"),
                group("Orrore", "Detestabile", "Repulsione"),
                group("Deluso", "Ripugnante", "Ribelle"),
                group("Disapprovazione", "Giudicante", "Disgustato")
            )
        ),
        MacroEmotion(
            id = "surprise",
            label = "Sorpresa",
            colorHex = 0xFFFFD45A,
            groups = listOf(
                group("Stupito", "Meravigliato"),
                group("Confuso", "Disilluso", "Perplesso"),
                group("Eccitato", "Desideroso", "Energico"),
                group("Spaventato", "Scoraggiato", "Scioccato")
            )
        )
    )

    fun byId(id: String): MacroEmotion =
        emotions.firstOrNull { it.id == id } ?: emotions.first()
}

private fun group(label: String, vararg children: String): EmotionGroup =
    EmotionGroup(label = label, children = children.toList())
