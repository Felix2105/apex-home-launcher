# APEX Home Launcher — Native Android source

A native Android home-screen launcher for the Samsung Galaxy A20 (Android 11), using a black/navy and gold design.

## Included
- Android HOME intent filter so Android can offer APEX Home as the default launcher.
- Searchable list of installed launcher apps.
- Seven workspace views: Command, Business HQ, AI Command, Documents, Communications, Health & Focus, Personal.
- Launches installed Android apps and opens the existing local APEX dashboards at `127.0.0.1:8080` and Business HQ at `127.0.0.1:8766`.
- No Firebase/Google Services secret is required.

## Build APK using GitHub Actions
1. Upload the *contents* of this ZIP to the root of `Felix2105/apex-home-launcher` (not the ZIP file itself).
2. Commit/push to `main` or `apex-launcher-build`, or open **Actions → Build APEX Home APK → Run workflow**.
3. Open the successful workflow run and download the artifact named `APEX-Home-debug-APK`.
4. Extract `app-debug.apk` on the phone and install it.
5. Open APEX Home, then Android Settings → Apps → Default apps → Home app (wording varies by Android version) and choose APEX Home.

## Notes
- The launcher is a standalone Android app; it does not replace or alter the existing APEX Linux Mobile services or backups.
- Business HQ and Linux dashboard buttons only work when those existing local services are running.
- App visibility is requested for the installed-app drawer. Android/Play distribution policies may impose additional requirements for `QUERY_ALL_PACKAGES`.
- This source has not been compiled or tested on the physical Galaxy A20 in this environment. The APK must be built by GitHub Actions and tested on-device before calling the release verified.
