package asg.bot;

import asg.struct.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Hanya sebagai contoh implementasi.
 */
public class BotGreedy extends BotBase {
    @Override
    protected byte searchMove(Board board) {
        List<Byte> moves = board.getEmptySquares();

        // Filter for moves with the largest eval value
        List<Byte> eligible = new ArrayList<>();
        int maxEval = 0;
        for (Byte move : moves) {
            int currEval = board.heuristic(move);
            if (currEval > maxEval) {
                maxEval = currEval;
                eligible.clear();
            }
            if (currEval == maxEval) {
                eligible.add(move);
            }
        }

        return eligible.get((int) (Math.random() * eligible.size()));
    }
}
