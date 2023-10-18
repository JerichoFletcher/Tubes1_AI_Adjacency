import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    private static final long[][] ZOBRIST_BOARD_BITSTRINGS;
    private static final long ZOBRIST_PLAYER_X_BITSTRING;
    static {
        // Inisialisasi bitstring acak untuk setiap kotak
        ZOBRIST_BOARD_BITSTRINGS = new long[PlayerMarks.values().length][Vars.BOARD_ROW_COUNT * Vars.BOARD_COL_COUNT];
        for (int i = 0; i < Vars.BOARD_ROW_COUNT * Vars.BOARD_COL_COUNT; i++) {
            for (int j = 0; j < ZOBRIST_BOARD_BITSTRINGS.length; j++) {
                ZOBRIST_BOARD_BITSTRINGS[j][i] = j == PlayerMarks.EMPTY.ordinal() ? 0 : ThreadLocalRandom.current().nextLong();
            }
        }
        // Inisialisasi bistring acak untuk giliran pemain
        ZOBRIST_PLAYER_X_BITSTRING = ThreadLocalRandom.current().nextLong();
    }

    private final int row, col;
    private final PlayerMarks[][] state;
    private final Set<Byte> emptySquares;
    private PlayerMarks currentPlayer;
    private int playerXScore;
    private int playerOScore;
    private int pliesLeft;
    private long zobristHash;

    /**
     * Menginisialisasi sebuah papan baru.
     *
     * @param currentPlayer Pemain yang memegang giliran sekarang pada permainan.
     * @param pliesLeft     Banyak ronde yang tersisa.
     * @throws IllegalArgumentException Jika {@code pliesLeft} tidak bernilai positif,
     *                                  atau jika {@code currentPlayer} bukan pemain valid.
     */
    public Board(PlayerMarks currentPlayer, int pliesLeft) {
        if (currentPlayer == PlayerMarks.EMPTY) throw new IllegalArgumentException("Current player cannot be empty");
        if (pliesLeft <= 0) throw new IllegalArgumentException("Number of plies left must be positive");

        this.row = Vars.BOARD_ROW_COUNT;
        this.col = Vars.BOARD_COL_COUNT;
        this.currentPlayer = currentPlayer;
        this.pliesLeft = pliesLeft;

        this.state = new PlayerMarks[row][col];
        for (int i = 0; i < row; i++) {
            Arrays.fill(this.state[i], PlayerMarks.EMPTY);
        }

        this.emptySquares = new HashSet<>();
        for (int r = 0; r < this.row; r++) {
            for (int c = 0; c < this.col; c++) {
                this.emptySquares.add(Coordinate.of(r, c));
            }
        }

        // Inisialisasi hash Zobrist
        this.zobristHash = this.currentPlayer == PlayerMarks.X ? ZOBRIST_PLAYER_X_BITSTRING : 0;
    }

    /**
     * Membentuk sebuah papan baru dengan keadaan yang sama dengan papan yang diberikan.
     *
     * @param other Papan yang akan disalin.
     * @throws NullPointerException Jika {@code other} bernilai {@code null}.
     */
    public Board(Board other) {
        if (other == null) throw new NullPointerException();

        this.row = other.row;
        this.col = other.col;
        this.currentPlayer = other.currentPlayer;
        this.pliesLeft = other.pliesLeft;

        this.state = new PlayerMarks[this.row][this.col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(other.state[i], 0, this.state[i], 0, col);
        }

        this.emptySquares = new HashSet<>(other.emptySquares);

        this.playerXScore = other.playerXScore;
        this.playerOScore = other.playerOScore;

        // Inisialisasi hash Zobrist
        this.zobristHash = other.zobristHash;
    }

    /**
     * Mengembalikan array berisi koordinat kotak kosong pada papan.
     *
     * @return List berisi koordinat kotak-kotak kosong pada papan.
     */
    public List<Byte> getEmptySquares() {
        return new ArrayList<>(this.emptySquares);
    }

    /**
     * Mengembalikan true jika roundsLeft = 0 atau isEmpty(getEmptySquares())
     *
     * @return true if roundsLeft = 0, false if otherwise
     */
    public Boolean isTerminal() {
        return this.pliesLeft == 0 || this.getEmptySquares().isEmpty();
    }

    /**
     * Mengembalikan nilai heuristik dari kualitas langkah yang diberikan oleh pemain yang memegang giliran saat ini.
     * @param move Koordinat yang diberikan, menggunakan format yang diberikan oleh {@code Coordinate}.
     * @return Nilai heuristik kualitas langkah ini. Nilai ini digunakan untuk pengurutan prioritas pemeriksaan langkah.
     */
    public int heuristic(byte move) {
        return this.heuristic(Coordinate.getX(move), Coordinate.getY(move));
    }

    /**
     * Mengembalikan nilai heuristik dari kualitas langkah yang diberikan oleh pemain yang memegang giliran saat ini.
     *
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @return Nilai heuristik kualitas langkah ini. Nilai ini digunakan untuk pengurutan prioritas pemeriksaan langkah.
     */
    public int heuristic(int row, int col) {
        // Jumlahkan nilai heuristik untuk kotak ini dan kotak-kotak yang bertetanggaan
        int count = heuristicPart(row, col);
        if (row > 0) count += heuristicPart(row - 1, col);
        if (col > 0) count += heuristicPart(row, col - 1);
        if (row < this.row - 1) count += heuristicPart(row + 1, col);
        if (col < this.col - 1) count += heuristicPart(row, col + 1);

        return count;
    }

    public PlayerMarks getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getPlayerXScore() {
        return this.playerXScore;
    }

    public int getPlayerOScore() {
        return this.playerOScore;
    }

    public int getPliesLeft() {
        return this.pliesLeft;
    }

    /**
     * Membaca markah pada koordinat yang diberikan.
     *
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @return Markah yang terletak di kotak pada koordinat yang diberikan.
     */
    public PlayerMarks getAt(int row, int col) {
        return this.state[row][col];
    }

    /**
     * Menuliskan markah pada koordinat yang diberikan.
     * Penulisan gagal jika kotak pada koordinat tersebut tidak kosong.
     *
     * @param row  Baris dari koordinat yang diberikan.
     * @param col  Kolom dari koordinat yang diberikan.
     * @param mark Markah yang akan dituliskan.
     * @throws IllegalArgumentException Jika {@code mark} merupakan markah kosong.
     * @throws IllegalStateException    Jika kotak yang dituju sudah terisi markah.
     */
    public void setAt(int row, int col, PlayerMarks mark) {
        /* TODO:
            Remove setAt() as a public method completely?
            Move board state initialization to constructor?
         */
        if (mark == PlayerMarks.EMPTY) throw new IllegalArgumentException("Mark is empty");
        if (this.state[row][col] != PlayerMarks.EMPTY) throw new IllegalStateException("Target square is not empty");
        this.setMark(row, col, mark);
    }

    /**
     * Melakukan aksi pada koordinat yang diberikan untuk pemain yang memegang giliran.
     *
     * @param move Koordinat yang dipilih.
     * @throws IllegalStateException Jika kotak yang dituju sudah terisi markah.
     */
    public void act(byte move) {
        this.act(Coordinate.getX(move), Coordinate.getY(move));
    }

    /**
     * Melakukan aksi pada koordinat yang diberikan untuk pemain yang memegang giliran.
     *
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @throws IllegalStateException Jika kotak yang dituju sudah terisi markah.
     */
    public void act(int row, int col) {
        PlayerMarks mark = this.currentPlayer;

        // Will throw if (row, col) is not empty
        this.setAt(row, col, mark);

        if (row > 0) this.setMark(row - 1, col, mark, true);
        if (col > 0) this.setMark(row, col - 1, mark, true);
        if (row < this.row - 1) this.setMark(row + 1, col, mark, true);
        if (col < this.col - 1) this.setMark(row, col + 1, mark, true);

        // Pass turn to opponent and conclude round
        this.switchTurn();
        this.pliesLeft--;
    }

    /**
     * Mengembalikan nilai hash Zobrist dari papan ini.
     *
     * @return Nilai hash Zobrist yang dihitung dari papan ini.
     */
    public long zobristHash() {
        return this.zobristHash;
    }

    private void setMark(int row, int col, PlayerMarks mark) {
        this.setMark(row, col, mark, false);
    }

    private void setMark(int row, int col, PlayerMarks mark, boolean skipEmpty) {
        PlayerMarks oldMark = this.state[row][col];
        if (skipEmpty && oldMark == PlayerMarks.EMPTY) return;

        this.state[row][col] = mark;
        this.emptySquares.remove(Coordinate.of(row, col));

        switch (oldMark) {
            case X -> this.playerXScore--;
            case O -> this.playerOScore--;
        }
        switch (mark) {
            case X -> this.playerXScore++;
            case O -> this.playerOScore++;
        }

        // Flip the hash on this square
        this.zobristHash ^= ZOBRIST_BOARD_BITSTRINGS[oldMark.ordinal()][row * 8 + col];
        this.zobristHash ^= ZOBRIST_BOARD_BITSTRINGS[mark.ordinal()][row * 8 + col];
    }

    private int heuristicPart(int row, int col) {
        int count = 0;
        PlayerMarks toCount = this.currentPlayer == PlayerMarks.X ? PlayerMarks.O : PlayerMarks.X;

        // Hitung banyak markah lawan yang bertetanggaan dengan kotak ini
        if (row > 0 && this.getAt(row - 1, col) == toCount) count++;
        if (col > 0 && this.getAt(row, col - 1) == toCount) count++;
        if (row < this.row - 1 && this.getAt(row + 1, col) == toCount) count++;
        if (col < this.col - 1 && this.getAt(row, col + 1) == toCount) count++;

        // Periksa apakah ada markah sendiri yang bertetanggaan secara diagonal dan ada kotak kosong
        // yang bertetanggaan dengan kedua kotak
        if (row > 0 && col > 0 && this.getAt(row - 1, col - 1) == this.currentPlayer &&
                (this.getAt(row - 1, col) == PlayerMarks.EMPTY || this.getAt(row, col - 1) == PlayerMarks.EMPTY)
        ) count--;
        if (row > 0 && col < this.col - 1 && this.getAt(row - 1, col + 1) == this.currentPlayer &&
                (this.getAt(row - 1, col) == PlayerMarks.EMPTY || this.getAt(row, col + 1) == PlayerMarks.EMPTY)
        ) count--;
        if (row < this.row - 1 && col > 0 && this.getAt(row + 1, col - 1) == this.currentPlayer &&
                (this.getAt(row + 1, col) == PlayerMarks.EMPTY || this.getAt(row, col - 1) == PlayerMarks.EMPTY)
        ) count--;
        if (row < this.row - 1 && col < this.col - 1 && this.getAt(row + 1, col + 1) == this.currentPlayer &&
                (this.getAt(row + 1, col) == PlayerMarks.EMPTY || this.getAt(row, col + 1) == PlayerMarks.EMPTY)
        ) count--;

        return count;
    }

    private void switchTurn() {
        this.currentPlayer = switch (this.currentPlayer) {
            case X -> PlayerMarks.O;
            case O -> PlayerMarks.X;
            case EMPTY -> throw new RuntimeException();
        };

        // Flip the player hash
        this.zobristHash ^= ZOBRIST_PLAYER_X_BITSTRING;
    }
}
