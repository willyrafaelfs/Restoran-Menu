# Implementation Plan - Enhancements and Fixes for Rona Rasa Restaurant App

This plan outlines the changes to improve the Home screen, Profile screen, Menu Detail screen, and Navigation animations.

## User Review Required

> [!NOTE]
> I will pick the first 3 items from the menu list to display as "Popular Menus" on the Home screen, as there is currently no popularity metric in the data model.

## Proposed Changes

### Home Screen Enhancements

#### [HomeScreen.kt](file:///C:/Willy/Kuliah/Semester 6/Matakuliah/Pemrograman Mobile/Tugas/UTS/Rona Rasa Restaurant/app/src/main/java/com/example/menurestoran/ui/screens/HomeScreen.kt)
- Add a `LaunchedEffect` to auto-scroll the `HorizontalPager` every 5 seconds.
- Implement a "Popular Menus" section using a `LazyRow` that displays a subset of menus.
- Remove the "Lihat Menu Lengkap" button.

---

### Profile Screen Enhancements

#### [EditProfileScreen.kt](file:///C:/Willy/Kuliah/Semester 6/Matakuliah/Pemrograman Mobile/Tugas/UTS/Rona Rasa Restaurant/app/src/main/java/com/example/menurestoran/ui/screens/EditProfileScreen.kt)
- Add a profile photo picker (circular) above or near the banner picker.
- Save the `profile_url` to `SharedPreferences`.

#### [ProfileScreen.kt](file:///C:/Willy/Kuliah/Semester 6/Matakuliah/Pemrograman Mobile/Tugas/UTS/Rona Rasa Restaurant/app/src/main/java/com/example/menurestoran/ui/screens/ProfileScreen.kt)
- Update the profile header to show the picked image (using `AsyncImage`) instead of the default `Storefront` icon.

---

### Menu Detail Screen Fixes

#### [DetailMenuScreen.kt](file:///C:/Willy/Kuliah/Semester 6/Matakuliah/Pemrograman Mobile/Tugas/UTS/Rona Rasa Restaurant/app/src/main/java/com/example/menurestoran/ui/screens/DetailMenuScreen.kt)
- Add `WindowInsets.statusBars` padding to the `TopAppBar` to prevent it from being covered by the status bar or bleeding incorrectly.
- Adjust the `Spacer` height and content padding to ensure the image does not overlap the title text in an unappealing way.
- Implement rating persistence by saving the rating to `SharedPreferences` (using key `rating_{id}`) whenever it's changed.
- Initialize the rating state from `SharedPreferences` on screen launch.

---

### Navigation Animations

#### [AppNavigation.kt](file:///C:/Willy/Kuliah/Semester 6/Matakuliah/Pemrograman Mobile/Tugas/UTS/Rona Rasa Restaurant/app/src/main/java/com/example/menurestoran/navigation/AppNavigation.kt)
- Standardize transitions for all main screens (Home, Menu, Profile) to use horizontal slide animations for consistency.

## Verification Plan

### Automated Tests
- I will perform a `gradle_build` to ensure no syntax errors are introduced.

### Manual Verification
- **Home Screen**: Verify the banner auto-slides. Verify the "Popular Menus" section exists and contains menu items. Verify the "Lihat Menu Lengkap" button is gone.
- **Profile Screen**: Change the profile photo in Edit Profile and verify it updates in the Profile screen.
- **Menu Detail**: Verify the image and status bar look correct. Verify the title is not covered by the image. Verify that setting a rating and returning to the screen persists the rating.
- **Navigation**: Verify that switching between Home, Menu, and Profile uses consistent horizontal slide animations.
