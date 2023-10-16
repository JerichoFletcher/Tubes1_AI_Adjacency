import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private final int row, col;
    private final PlayerMarks[][] state;
    private PlayerMarks currentPlayer;
    private int playerXScore;
    private int playerOScore;
    private int pliesLeft;

    /**
     * Menginisialisasi sebuah papan baru.
     * @param row Banyak baris pada papan.
     * @param col Banyak kolom pada papan.
     * @param currentPlayer Pemain yang memegang giliran sekarang pada permainan.
     * @param pliesLeft Banyak ronde yang tersisa.
     * @throws IllegalArgumentException Jika {@code row}, {@code col}, atau {@code pliesLeft} tidak bernilai positif,
     * atau jika {@code currentPlayer} bukan pemain valid.
     */
    public Board(int row, int col, PlayerMarks currentPlayer, int pliesLeft){
        if(row <= 0 || col <= 0)throw new IllegalArgumentException("Size must be at least 1x1");
        if(currentPlayer == PlayerMarks.EMPTY)throw new IllegalArgumentException("Current player cannot be empty");
        if(pliesLeft <= 0)throw new IllegalArgumentException("Number of plies left must be positive");

        this.row = row;
        this.col = col;
        this.currentPlayer = currentPlayer;
        this.pliesLeft = pliesLeft;

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
        this(other.row, other.col, other.currentPlayer, other.pliesLeft);
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
        return this.pliesLeft == 0 || this.getEmptySquares().isEmpty();
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

    public PlayerMarks getCurrentPlayer() {
        return this.currentPlayer;
    }
    public int getPlayerXScore(){
        return this.playerXScore;
    }
    public int getPlayerOScore(){
        return this.playerOScore;
    }
    public int getPliesLeft(){
        return this.pliesLeft;
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
        if(mark == PlayerMarks.EMPTY)throw new IllegalArgumentException("Mark is empty");
        if(this.state[row][col] != PlayerMarks.EMPTY)throw new IllegalStateException("Target square is not empty");
        this.setMark(row, col, mark);
    }

    /**
     * Melakukan aksi pada koordinat yang diberikan untuk pemain yang memegang giliran.
     * @param row Baris dari koordinat yang diberikan.
     * @param col Kolom dari koordinat yang diberikan.
     * @throws IllegalStateException Jika kotak yang dituju sudah terisi markah.
     */
    public void act(int row, int col){
        PlayerMarks mark = this.currentPlayer;

        // Will throw if (row, col) is not empty
        this.setAt(row, col, mark);

        if(row > 0)this.setMark(row - 1, col, mark, true);
        if(col > 0)this.setMark(row, col - 1, mark, true);
        if(row < this.row - 1)this.setMark(row + 1, col, mark, true);
        if(col < this.col - 1)this.setMark(row, col + 1, mark, true);

        // Pass turn to opponent and conclude round
        this.switchTurn();
        this.pliesLeft--;
    }

    private void setMark(int row, int col, PlayerMarks mark){
        this.setMark(row, col, mark, false);
    }

    private void setMark(int row, int col, PlayerMarks mark, boolean skipEmpty){
        PlayerMarks oldMark = this.state[row][col];
        if(skipEmpty && oldMark == PlayerMarks.EMPTY)return;

        this.state[row][col] = mark;

        switch(oldMark){
            case X -> this.playerXScore--;
            case O -> this.playerOScore--;
        }
        switch(mark){
            case X -> this.playerXScore++;
            case O -> this.playerOScore++;
        }
    }

    private void switchTurn(){
        this.currentPlayer = switch(this.currentPlayer){
            case X -> PlayerMarks.O;
            case O -> PlayerMarks.X;
            case EMPTY -> throw new RuntimeException();
        };
    }
}