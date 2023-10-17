import java.util.*;
import java.util.function.BooleanSupplier;

public class Minimax {
    private static int leafCount = 0, pruneCount = 0;

    public static byte findOne(Board board, BooleanSupplier stopf) {
        leafCount = pruneCount = 0;

        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -board.heuristic(move)));

        // Get the move that produces the board state with maximum evaluation score
        List<Byte> maxResult = new ArrayList<>();
        int maxResultValue = Integer.MIN_VALUE;

        // Initialize alpha and beta
        int a = Integer.MIN_VALUE, b = Integer.MAX_VALUE;

        for (byte move : moves) {
            if (stopf.getAsBoolean()) break;
            Board nextBoard = moveBoards.get(move);

            int score = minValue(nextBoard, stopf, a, b, board.getCurrentPlayer());
            System.out.printf("Found (%s, %s):%s with score %s\n", Coordinate.getX(move), Coordinate.getY(move), board.heuristic(move), score);
            if (score > a) {
                a = score;
                maxResult.clear();
            }
            if (score == a) {
                maxResult.add(move);
            }
        }

        // Select one of the result candidates at random
        System.out.printf("Checked %s leaf nodes; pruned %s branches\n", leafCount, pruneCount);
        return maxResult.get((int) (Math.random() * maxResult.size()));
    }

    private static int minValue(Board board, BooleanSupplier stopf, int a, int b, PlayerMarks searchingPlayer) {
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

        int score = Integer.MAX_VALUE;
        for (byte move : moves) {
            score = Math.min(score, maxValue(moveBoards.get(move), stopf, a, b, searchingPlayer));
            if (score < a) {
                pruneCount++;
                return score;
            }
            b = Math.min(b, score);
        }
        return score;
    }

    private static int maxValue(Board board, BooleanSupplier stopf, int a, int b, PlayerMarks searchingPlayer) {
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

        int score = Integer.MIN_VALUE;
        for (byte move : moves) {
            score = Math.max(score, minValue(moveBoards.get(move), stopf, a, b, searchingPlayer));
            if (score > b) {
                pruneCount++;
                return score;
            }
            a = Math.max(a, score);
        }
        return score;
    }

    public static void findAll(Tree<ReservationNode> tree, Board board) {
        findAll(tree, board, board.getCurrentPlayer(), true);
    }

    private static void findAll(Tree<ReservationNode> tree, Board board, PlayerMarks searchingPlayer, boolean isMax) {
        if (tree.getChildren().size() == 0) {
            // This node is terminal: calculate value directly
//            System.out.printf("Attempting to evaluate %s, %s\n", tree.getValue().action, tree.getValue().evaluationScore);

            Board leafBoard = new Board(board);
            List<Byte> actions = new ArrayList<>();

            // Simulate acting out this path on the board
            Tree<ReservationNode> currentTree = tree;
            while (currentTree.getParent() != null) {
                if (currentTree.getParent() != null)
                    actions.add(0, currentTree.getValue().action);
                currentTree = currentTree.getParent();
            }
            for (Byte action : actions) {
//                System.out.printf("  > Acting out %s...", action);
                leafBoard.act(action);
//                System.out.println(" done!");
            }

            // Calculate the evaluation score
            tree.getValue().evaluationScore = switch (searchingPlayer) {
                case X -> leafBoard.getPlayerXScore() - leafBoard.getPlayerOScore();
                case O -> leafBoard.getPlayerOScore() - leafBoard.getPlayerXScore();
                default -> throw new RuntimeException();
            };
        } else {
            // Perform minimax search
            if (isMax) {
                tree.getValue().evaluationScore = Integer.MIN_VALUE;
                for (Tree<ReservationNode> child : tree.getChildren()) {
                    findAll(child, board, searchingPlayer, false);
                    if (tree.getValue().evaluationScore < child.getValue().evaluationScore)
                        tree.getValue().evaluationScore = child.getValue().evaluationScore;
                }
            } else {
                tree.getValue().evaluationScore = Integer.MAX_VALUE;
                for (Tree<ReservationNode> child : tree.getChildren()) {
                    findAll(child, board, searchingPlayer, true);
                    if (tree.getValue().evaluationScore > child.getValue().evaluationScore)
                        tree.getValue().evaluationScore = child.getValue().evaluationScore;
                }
            }
        }
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
