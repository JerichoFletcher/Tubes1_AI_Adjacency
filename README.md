# Adjacency Strategy Game

<kbd>
  <img src="https://github.com/JerichoFletcher/Tubes1_AI_Adjacency/blob/main/Screenshot/gamePlay.png">
</kbd>

<hr>

### Introduction
Repositori ini mencakup perbaikan dari aplikasi "Adjacency," yang merupakan permainan papan interaktif dan strategis untuk dua pemain dengan tujuan meletakkan sebanyak mungkin pion dibandingkan dengan pion lawan. Dalam rangka meningkatkan tingkat kecerdasan permainan bot, repositori ini mengimplementasikan algoritma canggih, termasuk algoritma Minimax Alpha-Beta Prunning, algoritma local search, dan Genetic Algorithm. Dengan tambahan fitur-fitur ini, pemain dapat menghadapi tantangan yang lebih kuat dan memperdalam pemahaman mereka tentang konsep kecerdasan buatan dalam konteks permainan papan yang menarik.

<hr>

### Installation (IntelliJ)
Untuk memulai, silakan instal versi terbaru dari Java Development Kit (JDK) dan instal Integrated Development Environment (IDE) Java seperti <a href="https://www.jetbrains.com/idea/">IntelliJ</a>. Harap diperhatikan bahwa petunjuk penyebaran di bawah ini menggunakan IntelliJ sebagai IDE.

<hr>

### Deployment (IntelliJ)

1. Clone repositori ini menggunakan Git dengan menjalankan perintah berikut: `git clone https://github.com/JerichoFletcher/Tubes1_AI_Adjacency.git`, atau Anda dapat juga mengunduh repositorinya secara langsung.
2. Buka folder repositori ini melalui IntelliJ.
3. Siapkan JDK dengan pergi ke tab **File -> Struktur Proyek -> Tab Proyek**. Di dalam Tab Proyek, pada Project SDK, klik "New," lalu cari lokasi folder JDK yang Anda miliki.
4. CATATAN: Mulai dari JDK 11, JavaFX telah dihapus dan menjadi modul mandiri. Berkas-berkas JavaFX yang diperlukan untuk menjalankan program "Adjacency" terletak dalam folder repositori itu sendiri. Untuk mengaturnya, pergi ke File -> Struktur Proyek -> Tab Libraries. Pada Tab Libraries, tekan tombol +, cari folder "javafx-sdk/lib" di dalam repositori, dan tambahkan ke daftar perpustakaan.
5. PENTING: Buka Run -> Edit Configurations, dan pergi ke bagian VM options. Di sini, tambahkan jalur lengkap ke folder "lib" dalam direktori javafx-sdk di komputer Anda, lalu tambahkan baris berikut: `--add-modules=javafx.controls,javafx.fxml`. <br><br>
   Contohnya, saya menambahkan baris berikut ke opsi VM saya: `--module-path "C:\Jed's Work\CS Side Projects\Adjacency-Strategy-Game\javafx-sdk\lib" --add-modules=javafx.controls,javafx.fxml`
6. Buka kelas Main dalam antarmuka file IntelliJ.

<hr>

### Program Instructions
1. Jalankan kelas Main untuk memulai program, dan jendela input akan muncul. Masukan nama pemain (X) dan pemain (O) pada text box yang tersedia, lalu pilih peran untuk setiap pemain X dan O melalui menu dropdown dibawahnya yang terdiri dari Human, Minimax bot, Local search bot, dan Genetic Algorithm bot.
Lalu pilih jumlah ronde yang akan dimainkan (antara 2 sampai 28) melalui menu dropdown yang tersedia.
Kamu bisa memilih pemain mana yang akan bermain lebih dulu.
<br><br><kbd>
<img src="https://github.com/JerichoFletcher/Tubes1_AI_Adjacency/blob/main/Screenshot/inputScreen-1.png"></kbd>
<br><br>
<img src="https://github.com/JerichoFletcher/Tubes1_AI_Adjacency/blob/main/Screenshot/inputScreen-2.png"></kbd>
<br><br>
<img src="https://github.com/JerichoFletcher/Tubes1_AI_Adjacency/blob/main/Screenshot/inputScreen-3.png"></kbd>
<br><br>
2. Tekan tombol play, dan papan permainan serta papan score akan dimuat. Pemain pertama (Human atau bot) memulai permainan dengan memilih kotak yang kosong. Akibatnya, setiap marka pemain lawan yang berada disekitar kotak tersebut akan berubah menjadi marka miliknya.
3. Lalu pemain lawan akan bermain dengan cara yang sama. Proses ini akan dihitung sebagai satu ronde (pemain X dan pemain O telah mengambil giliran).
4. Permainan akan terus berlanjut sampai tidak ada lagi ronde yang tersisa. Diakhir permainan, pemain dengan score tertinggi akan menjadi pemenangnya.
<kbd>
  <img src="https://github.com/JerichoFletcher/Tubes1_AI_Adjacency/blob/main/Screenshot/minimax-human.png">
</kbd>

<hr>

### Notes
<ul>
  <li>Built with <a href="https://openjfx.io/">JavaFX</a></li>
  <li>Modified by ITB Graphics and AI Lab Assistant 2020</li>
</ul>

### Kontibutor
| NIM  | Nama |
| ------------- | ------------- |
| 13521059 | Arleen Chrysantha Gunardi |
| 13521107 | Jericho Russel Sebastian |
| 13521125 | Asyifa Nurul Shafira |
| 13521133 | Cetta Reswara Parahita |
