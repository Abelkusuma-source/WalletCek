FLOW KERJA MONEYTRACKER (READY TO EXECUTE)
Milestone 1 — Setup Project & Navigasi Dasar

Tujuan: aplikasi bisa run dan sudah ada struktur UI.

Setup project Compose Material3
Tambah dependency (Room, Navigation, ViewModel, Coroutines)
Buat struktur folder (data/ui/viewmodel/utils)
Buat Bottom Navigation: Home / Report / Settings
Buat screen kosong + NavGraph

✅ Output: App running + pindah tab berhasil

Milestone 2 — Database Room + Model Data

Tujuan: database siap dan struktur data benar.

Buat enum TransactionType (INCOME/EXPENSE)
Buat Entity:
CategoryEntity
TransactionEntity
Buat DAO Category & Transaction
Buat AppDatabase singleton
Buat Repository

✅ Output: Room database bisa menyimpan transaksi dan kategori

Milestone 3 — Default Kategori Otomatis

Tujuan: kategori default langsung tersedia setelah install.

Buat list kategori default income & expense
Saat aplikasi pertama run:
cek kategori kosong
jika kosong → insert kategori default

✅ Output: kategori default otomatis muncul di dropdown transaksi

Milestone 4 — Add Transaction (Create)

Tujuan: user bisa mencatat uang masuk & keluar.

Buat AddTransactionScreen (Material3 form)
Input amount, note, date
Pilih tipe income/expense
Dropdown kategori sesuai tipe
Validasi input + snackbar error
Simpan ke database via ViewModel

✅ Output: transaksi tersimpan di Room

Milestone 5 — Home Screen (Saldo + List Transaksi)

Tujuan: Home menampilkan data real-time.

Tampilkan total income bulan ini
Tampilkan total expense bulan ini
Hitung saldo = income - expense
Tampilkan list transaksi terbaru (LazyColumn)
Tambah FAB menuju AddTransactionScreen
Empty state jika transaksi kosong

✅ Output: Home menjadi dashboard utama

Milestone 6 — Delete Transaction

Tujuan: transaksi bisa dihapus dengan aman.

Tambahkan aksi delete (long press/swipe)
Dialog konfirmasi delete
Delete dari database + update UI otomatis

✅ Output: transaksi bisa dikelola (hapus)

Milestone 7 — Report Bulanan

Tujuan: laporan keuangan per bulan.

Filter transaksi berdasarkan bulan/tahun
Hitung income/expense bulan terpilih
Hitung saldo bulan terpilih
Breakdown per kategori (total per kategori)

✅ Output: ReportScreen menampilkan laporan bulanan

Milestone 8 — Settings (Kelola Kategori)

Tujuan: user bisa tambah kategori manual.

Buat SettingsScreen
Tambah kategori (dialog input)
Pilih type kategori (income/expense)
Tampilkan list kategori
Kategori default tidak bisa dihapus
Kategori custom bisa dihapus

✅ Output: kategori bisa ditambah user dan dipakai transaksi

Milestone 9 — Reset Data + Utilities

Tujuan: aplikasi siap digunakan jangka panjang.

Tambahkan tombol reset semua transaksi
Dialog konfirmasi reset
Buat util:
format rupiah Rp10.000
format tanggal

✅ Output: fitur maintenance selesai

Milestone 10 — Final Polish & Release

Tujuan: aplikasi siap dipakai / publish APK.

UI rapih Material3 (spacing, card, typography)
Snackbar sukses saat save/delete
Testing semua fitur (CRUD + report + kategori)
Generate signed APK/AAB
Buat icon & nama aplikasi MoneyTracker

✅ Output: APK stabil siap install

Urutan Pengerjaan (Sangat Disarankan)

1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10