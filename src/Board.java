import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final int ROW = 8;
    private static final int COL = 8;
    private PlayerMarks[][] state = new PlayerMarks[ROW][COL];
    private PlayerMarks firstPlayer;
    private int playerXScore;
    private int playerOScore;
    private int roundsLeft;

    /***
     * Mengembalikan array berisi koordinat kotak kosong pada papan.
     * @return List<Byte> emptySquares
     */
    public List<Byte> getEmptySquares() {
        ArrayList<Byte> emptySquares = new ArrayList<Byte>();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (this.state[i][j].equals(PlayerMarks.EMPTY)) {
                    emptySquares.add(Coordinate.concat((byte) i, (byte) j));
                }
            }
        }
        return emptySquares;
    }

    /***
     * Mengembalikan true jika roundsLeft = 0 atau isEmpty(getEmptySquares())
     * @return true if roundsLeft = 0, false if otherwise
     */
    public Boolean isTerminal(){
        return (this.roundsLeft == 0);
    }

    public int heuristic(int x, int y){
        //Mengembalikan banyaknya markah lawan yang berubah jika bot memilih aksi yang pada koordinat (x,y).
        return 0;
    }
}
