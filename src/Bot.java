import java.util.List;

public class Bot {
    public int[] move(Board board) {
        List<Byte> moves = board.getEmptySquares();

        // Choose a random move from a set of valid moves on the board
        byte move = moves.get((int)(Math.random() * moves.size()));

        return new int[]{Coordinate.getX(move), Coordinate.getY(move)};
    }
}
