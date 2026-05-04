package com.example.moodwheel.ui.screens

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ITALIAN)
private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN)
private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)

fun Long.formatTime(): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(timeFormatter)

fun LocalDate.formatDate(): String = format(dateFormatter)

fun LocalDate.formatMonth(): String = format(monthFormatter).replaceFirstChar { it.titlecase(Locale.ITALIAN) }

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
