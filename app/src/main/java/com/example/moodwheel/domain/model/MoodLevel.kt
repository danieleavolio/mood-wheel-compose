package com.example.moodwheel.domain.model

enum class MoodLevel(val value: Int, val label: String, val face: String) {
    VeryBad(1, "Molto male", ":("),
    Bad(2, "Male", ":-("),
    Neutral(3, "Neutro", ":|"),
    Good(4, "Bene", ":)"),
    VeryGood(5, "Molto bene", ":D");

    companion object {
        val displayOrder = listOf(VeryGood, Good, Neutral, Bad, VeryBad)

        fun fromValue(value: Int): MoodLevel =
            entries.firstOrNull { it.value == value } ?: Neutral
    }
}
