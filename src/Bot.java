import java.util.List;

public class Bot {
    public int[] move(Board board) {
        // create random move
        List<Byte> moves = board.getEmptySquares();
        byte move = moves.get((int)(Math.random() * moves.size()));
        return new int[]{Coordinate.getX(move), Coordinate.getY(move)};
//        return new int[]{(int) (Math.random()*8), (int) (Math.random()*8)};
    }
}
