import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private final int row, col;
    private final PlayerMarks[][] state;
    private PlayerMarks currentPlayer;
    private int playerXScore;
    private int playerOScore;
    private int roundsLeft;

    /**
     * Menginisialisasi sebuah papan baru.
     * @param row Banyak baris pada papan.
     * @param col Banyak kolom pada papan.
     * @param currentPlayer Pemain yang memegang giliran sekarang pada permainan.
     * @param roundsLeft Banyak ronde yang tersisa.
     * @throws IllegalArgumentException Jika {@code row}, {@code col}, atau {@code roundsLeft} tidak bernilai positif,
     * atau jika {@code currentPlayer} bukan pemain valid.
     */
    public Board(int row, int col, PlayerMarks currentPlayer, int roundsLeft){
        if(row <= 0 || col <= 0|| currentPlayer == PlayerMarks.EMPTY || roundsLeft <= 0)throw new IllegalArgumentException();

        this.row = row;
        this.col = col;
        this.currentPlayer = currentPlayer;

        this.state = new PlayerMarks[row][col];
        for(int i = 0; i < row; i++){
            Arrays.fill(this.state[i], PlayerMarks.EMPTY);
        }
    }

    /**
     * Membentuk sebuah papan baru dengan keadaan yang sama dengan papan yang diberikan.
     * @param other Papan yang akan disalin.
     * @throws NullPointerException Jika {@code other} bernilai {@code null}.
     */
    public Board(Board other){
        this(other.row, other.col, other.currentPlayer, other.roundsLeft);
        if(other == null)throw new NullPointerException();

        for(int i = 0; i < row; i++){
            System.arraycopy(other.state[i], 0, this.state[i], 0, col);
        }

        this.playerXScore = other.playerXScore;
        this.playerOScore = other.playerOScore;
    }

    /**
     * Mengembalikan array berisi koordinat kotak kosong pada papan.
     * @return List berisi koordinat kotak-kotak kosong pada papan.
     */
    public List<Byte> getEmptySquares() {
        ArrayList<Byte> emptySquares = new ArrayList<Byte>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (this.state[i][j].equals(PlayerMarks.EMPTY)) {
                    emptySquares.add(Coordinate.of((byte) i, (byte) j));
                }
            }
        }
        return emptySquares;
    }

    /**
     * Mengembalikan true jika roundsLeft = 0 atau isEmpty(getEmptySquares())
     * @return true if roundsLeft = 0, false if otherwise
     */
    public Boolean isTerminal(){
        return (this.roundsLeft == 0);
    }

    /**
     * Mengembalikan banyaknya markah lawan yang berubah jika bot memilih aksi pada koordinat yang diberikan.
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @return Banyak markah lawan yang bertetanggaan dengan kotak pada koordinat yang diberikan.
     */
    public int heuristic(int row, int col){
        return 0;
    }

    /**
     * Mengembalikan pemain yang memegang giliran sekarang.
     * @return Pemain yang memegang giliran sekarang, diwakili dengan markahnya.
     */
    public PlayerMarks getCurrentPlayer(){
        return this.currentPlayer;
    }

    /**
     * Membaca markah pada koordinat yang diberikan.
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @return Markah yang terletak di kotak pada koordinat yang diberikan.
     */
    public PlayerMarks getAt(int row, int col){
        return this.state[row][col];
    }

    /**
     * Menuliskan markah pada koordinat yang diberikan.
     * Penulisan gagal jika kotak pada koordinat tersebut tidak kosong.
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @param mark Markah yang akan dituliskan.
     * @throws IllegalArgumentException Jika {@code mark} merupakan markah kosong.
     * @throws IllegalStateException Jika kotak yang dituju sudah terisi markah.
     */
    public void setAt(int row, int col, PlayerMarks mark) {
        /* TODO:
            Remove setAt() as a public method completely?
            Move board state initialization to constructor?
         */
        if(mark == PlayerMarks.EMPTY)throw new IllegalArgumentException();
        if(this.state[row][col] != PlayerMarks.EMPTY)throw new IllegalStateException();
        this.state[row][col] = mark;
    }

    /**
     * Melakukan aksi pada koordinat yang diberikan untuk pemain yang memegang giliran.
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     */
    public void act(int row, int col){
        // TODO: Implement board act
        throw new UnsupportedOperationException();
    }
}
