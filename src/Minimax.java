import java.util.*;
import java.util.function.BooleanSupplier;

public class Minimax {
    private static int leafCount = 0;

    public static byte findOne(Board board, BooleanSupplier stopf) {
        leafCount = 0;

        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -moveBoards.get(move).heuristic(move)));

        // Get the move that produces the board state with maximum evaluation score
        List<Byte> maxResult = new ArrayList<>();
        int maxResultValue = Integer.MIN_VALUE;

        for (byte move : moves) {
            if (stopf.getAsBoolean()) break;
            Board nextBoard = moveBoards.get(move);

            int score = minValue(nextBoard, stopf, board.getCurrentPlayer());
            if (score > maxResultValue) {
                maxResultValue = score;
                maxResult.clear();
            }
            if (score == maxResultValue) {
                maxResult.add(move);
            }
        }

        // Select one of the result candidates at random
        System.out.printf("Checked %s leaf nodes\n", leafCount);
        return maxResult.get((int) (Math.random() * maxResult.size()));
    }

    private static int minValue(Board board, BooleanSupplier stopf, PlayerMarks searchingPlayer) {
        if (stopf.getAsBoolean() || board.isTerminal()) {
            leafCount++;
            return switch (searchingPlayer) {
                case X -> board.getPlayerXScore() - board.getPlayerOScore();
                case O -> board.getPlayerOScore() - board.getPlayerXScore();
                default -> throw new RuntimeException();
            };
        }

        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -moveBoards.get(move).heuristic(move)));

        int minResultValue = Integer.MAX_VALUE;
        for (byte move : moves) {
            int score = maxValue(moveBoards.get(move), stopf, searchingPlayer);
            minResultValue = Math.min(minResultValue, score);
        }
        return minResultValue;
    }

    private static int maxValue(Board board, BooleanSupplier stopf, PlayerMarks searchingPlayer) {
        if (stopf.getAsBoolean() || board.isTerminal()) {
            leafCount++;
            return switch (searchingPlayer) {
                case X -> board.getPlayerXScore() - board.getPlayerOScore();
                case O -> board.getPlayerOScore() - board.getPlayerXScore();
                default -> throw new RuntimeException();
            };
        }

        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -moveBoards.get(move).heuristic(move)));

        int maxResultValue = Integer.MIN_VALUE;
        for (byte move : moves) {
            int score = minValue(moveBoards.get(move), stopf, searchingPlayer);
            maxResultValue = Math.max(maxResultValue, score);
        }
        return maxResultValue;
    }

    private static Map<Byte, Board> generateNextBoardStates(Board board) {
        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = new HashMap<>();

        // Build a list of board states after the first move
        for (byte move : moves) {
            Board nextBoard = new Board(board);
            nextBoard.act(move);
            moveBoards.put(move, nextBoard);
        }

        return moveBoards;
    }
}
