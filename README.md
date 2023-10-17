# WaduhAI

[![Waduh☝😅](https://img.youtube.com/vi/q7VoelZ3Svo/mqdefault.jpg)](https://www.youtube.com/shorts/q7VoelZ3Svo)

_Waduh☝😅_

<kbd>
  <img src="https://github.com/ahnjedid/Adjacency-Strategy-Game/blob/master/screenshots/gamePlay.png">
  !!!GANTI y
</kbd>

<hr>

### Introduction
Repositori ini mencakup perbaikan dari aplikasi "Adjacency," yang merupakan permainan papan interaktif dan strategis untuk dua pemain dengan tujuan meletakkan sebanyak mungkin pion dibandingkan dengan pion lawan. Dalam rangka meningkatkan tingkat kecerdasan permainan bot, repositori ini mengimplementasikan algoritma canggih, termasuk algoritma Minimax Alpha-Beta Prunning, algoritma local search, dan Genetic Algorithm. Dengan tambahan fitur-fitur ini, pemain dapat menghadapi tantangan yang lebih kuat dan memperdalam pemahaman mereka tentang konsep kecerdasan buatan dalam konteks permainan papan yang menarik.

<hr>

### Installation (IntelliJ)
Untuk memulai, silakan instal versi terbaru dari Java Development Kit (JDK) dan instal Integrated Development Environment (IDE) Java seperti <a href="https://www.jetbrains.com/idea/">IntelliJ</a>. Harap diperhatikan bahwa petunjuk penyebaran di bawah ini menggunakan IntelliJ sebagai IDE.

<hr>

### Deployment (IntelliJ)

1. Clone repositori ini menggunakan Git dengan menjalankan perintah berikut: `git clone https://github.com/GAIB20/adversarial-adjacency-strategy-game.git`, atau Anda dapat juga mengunduh repositorinya secara langsung.
2. Buka folder repositori ini melalui IntelliJ.
3. Siapkan JDK dengan pergi ke tab **File -> Struktur Proyek -> Tab Proyek**. Di dalam Tab Proyek, pada Project SDK, klik "New," lalu cari lokasi folder JDK yang Anda miliki.
4. CATATAN: Mulai dari JDK 11, JavaFX telah dihapus dan menjadi modul mandiri. Berkas-berkas JavaFX yang diperlukan untuk menjalankan program "Adjacency" terletak dalam folder repositori itu sendiri. Untuk mengaturnya, pergi ke File -> Struktur Proyek -> Tab Libraries. Pada Tab Libraries, tekan tombol +, cari folder "javafx-sdk/lib" di dalam repositori, dan tambahkan ke daftar perpustakaan.
5. PENTING: Buka Run -> Edit Configurations, dan pergi ke bagian VM options. Di sini, tambahkan jalur lengkap ke folder "lib" dalam direktori javafx-sdk di komputer Anda, lalu tambahkan baris berikut: `--add-modules=javafx.controls,javafx.fxml`. <br><br>
   Contohnya, saya menambahkan baris berikut ke opsi VM saya: `--module-path "C:\Jed's Work\CS Side Projects\Adjacency-Strategy-Game\javafx-sdk\lib" --add-modules=javafx.controls,javafx.fxml`
6. Buka kelas Main dalam antarmuka file IntelliJ.

<hr>

### Program Instructions
1. Run the Main class to load the program, and the input window below will pop up. Input the names of Player (X) and Bot (O) into their respective text fields.
Then, select the number of rounds (a number between 2 and 28) to play using the dropdown menu.
You can make the Bot start first.
<br><br><kbd>
<img src="https://github.com/ahnjedid/Adjacency-Strategy-Game/blob/master/screenshots/inputScreen.png"></kbd>
<br><br>
2. Click Play, and the gameboard and scoreboard window will load. Player (X) starts the game by clicking on an empty button. Any adjacent O’s will change to X's as a result. 
3. Then, Bot (O) has their turn by also clicking on an empty button. Any adjacent X’s will change to O's as a result. NOTE: This process is counted as 1 round (Player and Bot both taking their turns).
4. The game will continue until there are no more rounds left to play. In the end, the player with the greater number of letters is the winner of the game.
<kbd>
  <img src="https://github.com/ahnjedid/Adjacency-Strategy-Game/blob/master/screenshots/endOfGame.png">
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
