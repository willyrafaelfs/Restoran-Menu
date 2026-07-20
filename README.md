# 🍽️ Rona Rasa Restaurant - Profil Menu (Aplikasi UTS)

Aplikasi **Rona Rasa Restaurant** adalah aplikasi Android berbasis **Jetpack Compose** yang dikembangkan sebagai proyek Ujian Tengah Semester (UTS) untuk mata kuliah Pemrograman Mobile. Aplikasi ini dirancang untuk memberikan pengalaman manajemen menu restoran yang modern, estetik, dan fungsional.

---

## 👤 Identitas Mahasiswa

- **Nama**: Willy Rafael F. Silalahi
- **NIM**: 23083000168
- **Kelas**: 6A2
- **Mata Kuliah**: Pemrograman Mobile
- **Semester**: 6
- **Program Studi**: Sistem Informasi
- **Instansi**: Universitas Merdeka Malang

---

## 🚀 Fitur Utama

Aplikasi ini mencakup berbagai fitur canggih untuk meningkatkan pengalaman pengguna:

### 🔹 Fitur Utama
- **CRUD Menu Lengkap**: Tambah, Lihat, Edit, dan Hapus menu restoran secara lokal.
- **Penyimpanan Persisten**: Menggunakan *SharedPreferences* dan *Gson* untuk menyimpan data menu dan profil restoran.
- **Manajemen Profil**: Pengguna dapat mengubah nama, alamat, deskripsi, banner, dan foto profil restoran.
- **Rating Menu**: Fitur rating interaktif pada detail menu yang tersimpan secara permanen.

### 🎨 UI/UX & Animasi Modern
- **Auto-Sliding Banner**: Banner promo di Home Screen yang berganti otomatis dengan transisi halus.
- **Smooth Navigation**: Transisi antar layar menggunakan animasi *Horizontal Slide* yang konsisten.
- **Dark/Light Mode**: Dukungan penuh untuk mode gelap dan terang yang dapat diganti secara *real-time*.
- **Parallax Header**: Efek visual dinamis pada layar detail menu.
- **Material 3 Components**: Menggunakan standar desain terbaru dari Google untuk tampilan yang bersih dan modern.

---

## 📂 Struktur Proyek

Berikut adalah struktur direktori utama dari aplikasi ini:

```text
app/src/main/java/com/example/menurestoran/
├── model/           # Data model (MenuItem) & Repository (SharedPreferences)
├── navigation/      # Navigasi antar layar (NavHost)
├── ui/              # Komponen UI
│   ├── screens/     # Implementasi setiap layar (Home, Menu, Detail, dll)
│   ├── theme/       # Konfigurasi tema (Color, Type, Theme)
│   └── utils/       # Helper untuk animasi dan efek visual
└── utils/           # Utility class (ImageHelper, CurrencyFormatter)
```

---

## 📸 Dokumentasi Aplikasi (Screenshots)

Berikut adalah visual dari aplikasi ini (klik untuk memperbesar jika didukung):

| Layar Utama | Layar Detail & Menu | Layar Profil & Edit |
| :---: | :---: | :---: |
| **Splash Screen** <br> <img src="screenshots/splash_screen.jpg" width="180" /> | **Menu Screen** <br> <img src="screenshots/menu_screen.jpg" width="180" /> | **Profil Restoran** <br> <img src="screenshots/profile_screen.jpg" width="180" /> |
| **Splash (Dark)** <br> <img src="screenshots/dark_mode_splash_screen.jpg" width="180" /> | **Detail Menu** <br> <img src="screenshots/detail_menu_screen.jpg" width="180" /> | **Edit Profil** <br> <img src="screenshots/profile_edit_screen.jpg" width="180" /> |
| **Home Screen** <br> <img src="screenshots/home_screen.jpg" width="180" /> | **Detail (V2)** <br> <img src="screenshots/detail_menu_screen2.jpg" width="180" /> | **Edit Menu** <br> <img src="screenshots/edit_menu_screen.jpg" width="180" /> |
| **Home (V2)** <br> <img src="screenshots/home_screen2.jpg" width="180" /> | **Tambah Menu** <br> <img src="screenshots/form_menu_screen.jpg" width="180" /> | |
| **Home (Dark)** <br> <img src="screenshots/dark_mode_home_screen.jpg" width="180" /> | | |
| **Home (Dark V2)** <br> <img src="screenshots/dark_mode_home_screen2.jpg" width="180" /> | | |

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Navigation Compose
- **Image Loading**: Coil
- **Data Serialization**: Gson
- **Storage**: SharedPreferences & Internal Storage

---

## ⚙️ Cara Menjalankan Project

1. **Clone Repository**
   ```bash
   git clone https://github.com/willyrafaelfs/Pemrograman-Mobile-UTS-Aplikasi-Restoran.git
   ```
2. **Buka di Android Studio**
   - Pilih *Open an Existing Project*.
   - Arahkan ke folder hasil clone.
3. **Sync Gradle**
   - Pastikan koneksi internet aktif untuk mengunduh dependensi (Coil, Gson, dll).
4. **Run Aplikasi**
   - Jalankan pada Emulator atau Device fisik (Min SDK 24).

---

© 2026 - Willy Rafael F. Silalahi - UTS Pemrograman Mobile
