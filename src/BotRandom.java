import java.util.List;

/**
 * Hanya sebagai contoh implementasi.
 */
public class BotRandom extends BotBase {
    @Override
    protected byte searchMove(Board board) {
        List<Byte> moves = board.getEmptySquares();

        // Choose a random move from a set of valid moves on the board
        byte move = moves.get((int)(Math.random() * moves.size()));

        return move;
    }
}
