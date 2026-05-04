# Build Without Android Studio

The easiest route is GitHub Actions. It builds the APK in the cloud, so your PC does not need Android Studio, Gradle, or Android SDK installed.

## Cloud Build

1. Create a GitHub repository.
2. Upload or push the contents of this folder:

```text
C:\Users\DanieleAvolio\Agile\Code\mood-wheel-compose
```

3. Open the GitHub repository.
4. Go to **Actions**.
5. Open **Build Android Debug APK**.
6. Click **Run workflow**.
7. When the build finishes, download the artifact named:

```text
mood-wheel-debug-apk
```

Inside it you will find:

```text
app-debug.apk
```

## Install On Phone

Copy `app-debug.apk` to your Android phone and open it.

If Android blocks the install, enable:

```text
Settings -> Security -> Install unknown apps
```

The exact label changes by Android version and phone brand.

## Local Build Without winget

If you still want to build locally, use the Android command-line tools ZIP from Google, not `winget`.

Official page:

```text
https://developer.android.com/studio#command-tools
```

After extracting, use the full path to `sdkmanager.bat`, for example:

```powershell
& "$HOME\AppData\Local\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root="$HOME\AppData\Local\Android\Sdk" "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

If license acceptance fails in PowerShell, try Command Prompt:

```cmd
"%LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root="%LOCALAPPDATA%\Android\Sdk" --licenses
```

If that still fails, the cloud build above avoids this problem entirely.
