package com.example.moodwheel

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ScreenshotCaptureTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val device = UiDevice.getInstance(instrumentation)
    private val packageName = context.packageName
    private val screenshotDir = File(context.getExternalFilesDir(null), "screenshots")

    @Test
    fun captureMainUiRoadmapScreens() {
        screenshotDir.mkdirs()

        launch(onboardingDone = false, darkTheme = false)
        screenshot("onboarding_1_light")
        clickText("Continua")
        screenshot("onboarding_2_light")
        clickText("Continua")
        screenshot("onboarding_3_light")

        launch(onboardingDone = true, darkTheme = false)
        screenshot("home_light")
        clickText("Calendario")
        screenshot("calendar_light")
        clickText("Statistiche")
        screenshot("stats_week_light")
        clickText("Mese")
        screenshot("stats_month_light")
        clickText("Anno")
        screenshot("stats_year_light")
        clickText("Momenti")
        screenshot("moments_light")
        clickText("Profilo")
        screenshot("profile_light")

        launch(onboardingDone = true, darkTheme = true)
        screenshot("home_dark")
        clickText("Calendario")
        screenshot("calendar_dark")
        clickText("Statistiche")
        screenshot("stats_dark")
        clickText("Profilo")
        screenshot("profile_dark")

        launch(onboardingDone = true, darkTheme = false)
        clickText("Nuovo check-in")
        screenshot("checkin_1_mood")
        clickText("Bene")
        clickText("Avanti")
        tapEmotionWheelSegment()
        screenshot("checkin_2_emotion")
        clickText("Avanti")
        screenshot("checkin_3_when")
        tapTimeTile()
        screenshot("checkin_3_time_sheet")
        clickText("Conferma")
        clickText("Avanti")
        screenshot("checkin_4_note")
    }

    private fun launch(onboardingDone: Boolean, darkTheme: Boolean) {
        context.getSharedPreferences("mood_wheel_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_done", onboardingDone)
            .putString("theme_mode", if (darkTheme) "dark" else "light")
            .commit()

        device.executeShellCommand("am force-stop $packageName")
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            ?: error("No launcher intent for $packageName")
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), 10_000)
        device.waitForIdle()
        Thread.sleep(1_200)
    }

    private fun clickText(text: String) {
        val objectByExactText = device.findObject(By.text(text))
        val objectByPartialText = objectByExactText ?: device.findObject(By.textContains(text))
        objectByPartialText?.click()
        device.waitForIdle()
        Thread.sleep(650)
    }

    private fun screenshot(name: String) {
        device.waitForIdle()
        Thread.sleep(250)
        device.takeScreenshot(File(screenshotDir, "$name.png"))
    }

    private fun tapEmotionWheelSegment() {
        device.click((device.displayWidth * 0.72f).toInt(), (device.displayHeight * 0.43f).toInt())
        device.waitForIdle()
        Thread.sleep(650)
    }

    private fun tapTimeTile() {
        device.click((device.displayWidth * 0.38f).toInt(), (device.displayHeight * 0.47f).toInt())
        device.waitForIdle()
        Thread.sleep(650)
    }
}
