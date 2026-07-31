# Build Setup Guide

This fork of ArchiveTune requires some one-time setup to build signed APKs locally and via GitHub Actions.

## 1. Generate the Release Keystore (one-time, local)

You need a Java installation (`keytool`) — use the one bundled with Android Studio or any JDK 17+.

Run this command (replace paths if needed):

```bash
# On Windows (Android Studio bundled JDK — adjust path to your AS version)
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" \
  -genkeypair -v \
  -keystore app/keystore/release.keystore \
  -alias archivetune-key \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass "ZR5AhbceOImg2THtS0iGks3n" \
  -keypass "job2hpyt7I09nCduY1SgXv4x" \
  -dname "CN=ArchiveTune Fork, OU=Mobile, O=Personal, L=Unknown, ST=Unknown, C=US"
```

> **⚠️ Save your credentials securely.** They are shown here once and in `keystore.properties`.  
> The `keystore.properties` file and `app/keystore/` directory are gitignored — never commit them.

### Generated Credentials

| Field | Value |
|---|---|
| Key alias | `archivetune-key` |
| Key password | `job2hpyt7I09nCduY1SgXv4x` |
| Keystore password | `ZR5AhbceOImg2THtS0iGks3n` |

The `keystore.properties` file at the repo root is already pre-filled with these values.

---

## 2. Build Locally (Debug)

```bash
./gradlew assembleGmsMobileUniversalDebug
```

No secrets needed for a debug build — all optional features simply degrade gracefully.

## 3. Build Locally (Release, signed)

After generating the keystore above:

```bash
./gradlew assembleGmsMobileUniversalRelease
```

The build reads credentials from `keystore.properties` automatically.

---

## 4. GitHub Actions (CI) Setup

Add these secrets in **Settings → Secrets and variables → Actions**:

### 🔴 Required for signed release builds

| Secret name | How to get it | Notes |
|---|---|---|
| `KEYSTORE` | `base64 app/keystore/release.keystore` | Base64-encoded keystore file |
| `KEY_ALIAS` | `archivetune-key` | Alias you used in keytool |
| `KEYSTORE_PASSWORD` | `ZR5AhbceOImg2THtS0iGks3n` | Store password |
| `KEY_PASSWORD` | `job2hpyt7I09nCduY1SgXv4x` | Key password |

> To get the base64 value on Linux/macOS: `base64 -w 0 app/keystore/release.keystore`  
> On Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("app\keystore\release.keystore"))`

### 🟡 Optional — features degrade gracefully without these

| Secret name | Feature powered | Impact if missing |
|---|---|---|
| `LASTFM_API_KEY` | Last.fm scrobbling | Scrobbling disabled, build succeeds |
| `LASTFM_SECRET` | Last.fm auth | Scrobbling disabled, build succeeds |
| `EXTRACTOR_BEARER` | `moriextractor` module | Falls back silently |
| `TOGETHER_BEARER_TOKEN` | AI lyrics translation | Feature disabled silently |
| `CANVAS_BEARER_TOKEN` | Spotify Canvas backgrounds | Feature disabled silently |
| `API_BEARER_TOKEN` | ArchiveTune data server auth | Falls back to unauthenticated calls |
| `START_IO_APP_ID` | Start.io support ads (GMS only) | Falls back to test ad ID |

> **`START_IO_APP_ID` is special:** Release GMS builds will **fail at configure time** without it  
> (there's a `validateStartIoReleaseConfiguration` Gradle task that enforces this).  
> You can either get your own Start.io app ID, or add it with an empty value to skip validation.

---

## 5. App Identity / Branding Note

The upstream ArchiveTune™ name, logo, and branding are explicitly **NOT covered by GPL-3.0**.  
If you plan to distribute this fork publicly, you must change:

- `applicationId` in `app/build.gradle.kts` (currently `moe.rukamori.archivetune`)
- App name in `app/src/main/res/values/app_name.xml`
- App icon resources
- Discord OAuth Application ID (`DISCORD_APPLICATION_ID` in `local.properties` or env)

For **personal use only**, this is not required.

---

## 6. Discord Application ID

The hardcoded `DISCORD_APPLICATION_ID` (`1165706613961789445`) is the upstream's Discord Developer app.

To use your own (recommended for personal forks):
1. Go to https://discord.com/developers/applications → New Application
2. Copy the Application ID
3. Add to `local.properties`: `DISCORD_APPLICATION_ID=your_id_here`
   OR set as a GitHub secret / env var

The redirect scheme is automatically derived as `discord-{APPLICATION_ID}`.
