import java.util.*;
import java.util.function.BooleanSupplier;

public class Minimax {
    // 0 = No debug; 1 = Brief; 2 = Verbose
    private static final int DEBUG = 1;

    private static int leafCount = 0, pruneCount = 0;
    private static final Map<Integer, SearchResult> lastBestMoveFromPosition = new HashMap<>();

    public static byte startSearch(Board board, BooleanSupplier stopF, int maxDepth) {
        lastBestMoveFromPosition.clear();

        if (DEBUG >= 1) System.out.printf("Starting search up to depth %s\n", maxDepth);
        int initialDepth = Math.min(maxDepth, 2);
        SearchResult result = findOne(board, stopF, initialDepth);
        for (int depth = initialDepth + 2; depth < maxDepth; depth += 2) {
            if (stopF.getAsBoolean()) break;
            SearchResult currentResult = findOne(board, stopF, depth);
            result = !stopF.getAsBoolean() || currentResult.evaluation > result.evaluation ? currentResult : result;
        }
        if (!stopF.getAsBoolean() && initialDepth != maxDepth) {
            SearchResult currentResult = findOne(board, stopF, maxDepth);
            result = !stopF.getAsBoolean() || currentResult.evaluation > result.evaluation ? currentResult : result;
        }

        if (DEBUG >= 1) System.out.printf("└-- Stopped search; found best move is %s with score %s\n", Coordinate.toString(result.move), result.evaluation);
        return result.move;
    }

    private static SearchResult findOne(Board board, BooleanSupplier stopF, int depth) {
        if (DEBUG >= 1) System.out.printf("├-- Starting search with depth %s\n", depth);
        leafCount = pruneCount = 0;

        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -board.heuristic(move)));

        // Get the move that produces the board state with maximum evaluation score
        List<Byte> maxResult = new ArrayList<>();

        // Initialize alpha and beta
        int a = Integer.MIN_VALUE, b = Integer.MAX_VALUE;

        // Check for the best move from the last iteration first
        if (lastBestMoveFromPosition.containsKey(board.hashCode())) {
            byte move = lastBestMoveFromPosition.get(board.hashCode()).move;
            if (moves.contains(move)) {
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
                if (DEBUG >= 1) System.out.printf("|   ├-- Prioritizing search %s first as it was the last best move\n", Coordinate.toString(move));
            }
        }

        for (byte move : moves) {
            if (stopF.getAsBoolean()) break;
            Board nextBoard = moveBoards.get(move);

            int score = minValue(nextBoard, stopF, a, b, board.getCurrentPlayer(), depth - 1);
            if (DEBUG >= 2) System.out.printf("|   |   |   └-- Evaluated %s [H = %s] with score %s\n", Coordinate.toString(move), board.heuristic(move), score);
            if (score > a) {
                a = score;
                maxResult.clear();
                if (DEBUG >= 1) System.out.printf("|   |   └-- Current best is now %s [H = %s] with score %s\n", Coordinate.toString(move), board.heuristic(move), score);
            }
            if (score == a) {
                maxResult.add(move);
            }
        }

        // Store the current best move for the next search deepening
        byte selectedMove = maxResult.get((int) (Math.random() * maxResult.size()));
        SearchResult result = new SearchResult(selectedMove, a);
        lastBestMoveFromPosition.put(board.hashCode(), result);

        if (DEBUG >= 1) System.out.printf("|   └-- Visited %s leaf nodes; pruned %s branches; found %s with score %s\n", leafCount, pruneCount, Coordinate.toString(selectedMove), a);
        return result;
    }

    private static int minValue(Board board, BooleanSupplier stopF, int a, int b, PlayerMarks searchingPlayer, int depth) {
        if (stopF.getAsBoolean() || depth == 0 || board.isTerminal()) {
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
        byte bestMove = 0;

        // Check for the best move from the last iteration first
        if (lastBestMoveFromPosition.containsKey(board.hashCode())) {
            byte move = lastBestMoveFromPosition.get(board.hashCode()).move;
            if (moves.contains(move)) {
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
            }
        }

        for (byte move : moves) {
            int checkScore = maxValue(moveBoards.get(move), stopF, a, b, searchingPlayer, depth - 1);
            if (score > checkScore) {
                score = checkScore;
                bestMove = move;
            }
            if (score < a) {
                pruneCount++;
                break;
            }
            b = Math.min(b, score);
        }

        lastBestMoveFromPosition.put(board.hashCode(), new SearchResult(bestMove, score));
        return score;
    }

    private static int maxValue(Board board, BooleanSupplier stopF, int a, int b, PlayerMarks searchingPlayer, int depth) {
        if (stopF.getAsBoolean() || depth == 0 || board.isTerminal()) {
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
        byte bestMove = 0;

        // Check for the best move from the last iteration first
        if (lastBestMoveFromPosition.containsKey(board.hashCode())) {
            byte move = lastBestMoveFromPosition.get(board.hashCode()).move;
            if (moves.contains(move)) {
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
            }
        }

        for (byte move : moves) {
            int checkScore = minValue(moveBoards.get(move), stopF, a, b, searchingPlayer, depth - 1);
            if (score < checkScore) {
                score = checkScore;
                bestMove = move;
            }
            if (score > b) {
                pruneCount++;
                break;
            }
            a = Math.max(a, score);
        }

        lastBestMoveFromPosition.put(board.hashCode(), new SearchResult(bestMove, score));
        return score;
    }

    public static void evaluateTree(Tree<ReservationNode> tree, Board board) {
        evaluateTree(tree, board, board.getCurrentPlayer(), true);
    }

    private static void evaluateTree(Tree<ReservationNode> tree, Board board, PlayerMarks searchingPlayer, boolean isMax) {
        if (tree.getChildren().size() == 0) {
            // This node is terminal: calculate value directly
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
                leafBoard.act(action);
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
                    evaluateTree(child, board, searchingPlayer, false);
                    if (tree.getValue().evaluationScore < child.getValue().evaluationScore)
                        tree.getValue().evaluationScore = child.getValue().evaluationScore;
                }
            } else {
                tree.getValue().evaluationScore = Integer.MAX_VALUE;
                for (Tree<ReservationNode> child : tree.getChildren()) {
                    evaluateTree(child, board, searchingPlayer, true);
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

    private static class SearchResult {
        public byte move;
        public int evaluation;

        public SearchResult(byte move, int evaluation) {
            this.move = move;
            this.evaluation = evaluation;
        }
    }
}
