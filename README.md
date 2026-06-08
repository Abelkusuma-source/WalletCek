# Wallet Cek 🪙

**Wallet Cek** adalah aplikasi manajemen keuangan pribadi modern berbasis Android yang dirancang untuk membantu pengguna mencatat transaksi, mengelola hutang, dan menganalisis kesehatan finansial dengan mudah dan cerdas.

---

## 🚀 1. Apa itu Wallet Cek?
Wallet Cek bukan sekadar pencatat pengeluaran biasa. Aplikasi ini mengombinasikan kekuatan **Local First Storage** dengan **Cloud Synchronization** dan teknologi **AI OCR** untuk memberikan pengalaman pencatatan keuangan yang tanpa hambatan (*frictionless*). Pengguna dapat memantau saldo, melihat laporan visual, dan mendapatkan pengingat hutang secara real-time.

---

## ✨ 2. Fitur Utama
*   **Smart Receipt Scanning (AI OCR)**: Masukkan transaksi hanya dengan memfoto struk belanja. Sistem secara otomatis mendeteksi nominal harga.
*   **Share to Wallet Cek**: Dukungan penuh untuk struk digital. Cukup klik "Share" pada screenshot, PDF, atau file teks dari aplikasi lain langsung ke Wallet Cek.
*   **Debt & Receivable Management**: Kelola hutang dan piutang dengan pelacakan status (Lunas/Belum Lunas).
*   **Automatic Cloud Sync**: Sinkronisasi data otomatis ke Firebase Firestore agar data tetap aman meskipun berganti perangkat.
*   **Financial Health Indicator**: Penilaian otomatis kondisi keuangan Anda (Stable, Warning, atau Critical) berdasarkan rasio pemasukan dan pengeluaran.
*   **Hybrid Offline-Online**: Aplikasi tetap berfungsi penuh tanpa internet dan akan sinkron otomatis saat kembali online (didukung oleh WorkManager).
*   **Push Notifications**: Pengingat tagihan dan hutang melalui Firebase Cloud Messaging.

---

## 🛠️ 3. Tech Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Modern Declarative UI)
*   **Database**: 
    *   Lokal: Room Database (SQLite)
    *   Cloud: Firebase Firestore
*   **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
*   **AI/OCR**: Google ML Kit (Text Recognition)
*   **Background Processing**: WorkManager
*   **Dependency Management**: Gradle Version Catalog (.toml)
*   **PDF Engine**: PDFBox Android
*   **Authentication**: Firebase Auth (Google Sign-In)

---

## ⚙️ 4. Cara Menjalankan
1.  **Clone Repository**:
    ```bash
    git clone https://github.com/username/WalletCek.git
    ```
2.  **Buka di Android Studio**: Pastikan menggunakan versi Ladybug atau yang lebih baru.
3.  **Firebase Setup**: 
    *   Buat project baru di [Firebase Console](https://console.firebase.google.com/).
    *   Download `google-services.json` dan letakkan di dalam folder `app/`.
4.  **Build & Run**: Hubungkan perangkat Android atau gunakan Emulator, lalu tekan **Run**.

---

## 🔑 5. Environment Variables
Aplikasi ini menggunakan konfigurasi Firebase. Pastikan variabel berikut dikonfigurasi di Firebase Console & `google-services.json`:
*   `google_app_id`
*   `project_id`
*   `api_key`
*   `web_client_id` (Untuk Google Sign-In, pastikan SHA-1 terdaftar di Firebase Settings).

---

## 🏗️ 6. Arsitektur Sistem
Aplikasi ini menggunakan pola **Clean Architecture** sederhana:
*   **View (Compose)**: Menangani tampilan dan interaksi pengguna.
*   **ViewModel**: Mengelola state UI dan menjembatani View dengan Repository.
*   **Repository**: Logika pemilihan sumber data (apakah mengambil dari Room lokal atau Firestore cloud).
*   **Data Sources**: Room (Local DAO) & Firestore (Network Service).

---

## 🗺️ 7. Roadmap
- [x] Integrasi AI OCR untuk Struk Kertas
- [x] Dukungan Digital Receipt (Share Intent PDF/Image)
- [x] Auto-sync Background (WorkManager)
- [ ] Visualisasi Grafik Laporan (Pie/Bar Chart)
- [ ] Fitur Budgeting (Anggaran Bulanan)
- [ ] Export Data ke Excel/CSV
- [ ] Multi-Currency Support

---

**Developed with ❤️ for better financial habits.**
