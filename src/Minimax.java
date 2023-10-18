import java.util.*;
import java.util.function.BooleanSupplier;

public class Minimax {
    // 0 = No debug; 1 = Brief; 2 = Verbose
    private static final int DEBUG = 1;

    private static int
            leafCount = 0,
            pruneCount = 0,
            lastBestMoveCacheHitCount = 0,
            transpositionMapHitCount = 0;
    private static final Map<Long, ActionNode>
            lastBestMoveCache = new HashMap<>(),
            transpositionMap = new HashMap<>();

    /**
     * Melakukan pencarian IDS untuk mencari langkah terbaik dalam beberapa iterasi dengan kedalaman yang berbeda,
     * Mengembalikan langkah terbaik yang ditemukan apabila ditemukan langkah paling minimum atau ketika waktu yang disediakan telah habis.
     * @param board Kondisi terkini papan permainan.
     * @param interrupt Timer untuk penghitungan waktu pencarian solusi.
     * @param maxDepth Kedalaman pohon yang dihitung dari pilihan banyaknya ronde permainan.
     * @return {@code true} jika pencarian bot sudah dihentikan.
     */
    public static byte startSearch(Board board, BooleanSupplier interrupt, int maxDepth) {
        lastBestMoveCache.clear();
        transpositionMap.clear();

        if (DEBUG >= 1) System.out.printf("Starting search up to depth %s\n", maxDepth);
        int initialDepth = Math.min(maxDepth, 2);
        ActionNode result = findOne(board, interrupt, initialDepth);

        for (int depth = initialDepth + 2; depth < maxDepth; depth += 2) {
            if (interrupt.getAsBoolean()) break;
            ActionNode currentResult = findOne(board, interrupt, depth);
            result = !interrupt.getAsBoolean() || currentResult.evaluationScore > result.evaluationScore ? currentResult : result;
        }

        if (!interrupt.getAsBoolean() && initialDepth != maxDepth) {
            ActionNode currentResult = findOne(board, interrupt, maxDepth);
            result = !interrupt.getAsBoolean() || currentResult.evaluationScore > result.evaluationScore ? currentResult : result;
        }

        if (DEBUG >= 1) System.out.printf("└-- Stopped search; found best move is %s with score %s\n", Coordinate.toString(result.action), result.evaluationScore);
        return result.action;
    }

    private static ActionNode findOne(Board board, BooleanSupplier interrupt, int depth) {
        if (DEBUG >= 1) System.out.printf("├-- Starting search with depth %s\n", depth);

        // Clean transposition cache and statistic counters
        leafCount = pruneCount = lastBestMoveCacheHitCount = transpositionMapHitCount = 0;
        transpositionMap.clear();

        // Generate all predecessors of the initial board state
        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -board.heuristic(move)));

        // Get the move that produces the board state with maximum evaluation score
        List<Byte> maxResult = new ArrayList<>();

        // Initialize alpha and beta
        int a = Integer.MIN_VALUE, b = Integer.MAX_VALUE;

        // Check for the best move from the last iteration first
        if (lastBestMoveCache.containsKey(board.zobristHash())) {
            byte move = lastBestMoveCache.get(board.zobristHash()).action;
            if (moves.contains(move)) {
                lastBestMoveCacheHitCount++;
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
                if (DEBUG >= 1) System.out.printf("|   ├-- Prioritizing search %s first as it was the last best move\n", Coordinate.toString(move));
            }
        }

        // Begin alpha-beta pruning search and keep track of all moves with the best evaluation score
        for (byte move : moves) {
            if (interrupt.getAsBoolean()) {
                if (DEBUG >= 1) System.out.println("|   |   └-- SEARCH INTERRUPTED!");
                break;
            }
            Board nextBoard = moveBoards.get(move);

            int score = minValue(nextBoard, interrupt, a, b, board.getCurrentPlayer(), depth - 1);
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
        ActionNode result = new ActionNode(a, selectedMove);
        lastBestMoveCache.put(board.zobristHash(), result);

        if (DEBUG >= 1) System.out.printf("""
                |   └-- Visited %s leaf nodes; pruned %s branches
                |       Best move cache size: %s; cache hits: %s
                |       Transposition map size: %s; transposition hits: %s
                |       Selected best move is %s with score %s
                """,
                leafCount, pruneCount,
                lastBestMoveCache.size(), lastBestMoveCacheHitCount,
                transpositionMap.size(), transpositionMapHitCount,
                Coordinate.toString(selectedMove), a
        );
        return result;
    }

    /**
     * Mencari pilihan minimal dalam permainan dalam sudut pandang pemain lawan
     * @param board Kondisi terkini papan permainan.
     * @param interrupt Timer untuk penghitungan waktu pencarian solusi.
     * @param a Nilai alfa dari pohon permainan.
     * @param b Nilai beta dari pohon permainan.
     * @param searchingPlayer Player yang sedang melakukan pencarian.
     * @param depth Kedalaman pohon yang dihitung dari pilihan banyaknya ronde permainan.
     * @return result hasil pencarian dengan kedalaman tertentu.
     */

    private static int minValue(Board board, BooleanSupplier interrupt, int a, int b, PlayerMarks searchingPlayer, int depth) {
        // End search if the maximum depth is reached or this board state is a terminal state
        if (interrupt.getAsBoolean() || depth == 0 || board.isTerminal()) {
            leafCount++;
            return switch (searchingPlayer) {
                case X -> board.getPlayerXScore() - board.getPlayerOScore();
                case O -> board.getPlayerOScore() - board.getPlayerXScore();
                default -> throw new RuntimeException();
            };
        }

        // If this position has been evaluated, return the value from the transposition map
        if (transpositionMap.containsKey(board.zobristHash())) {
            transpositionMapHitCount++;
            return transpositionMap.get(board.zobristHash()).evaluationScore;
        }

        // Generate all predecessors of the current board state
        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -moveBoards.get(move).heuristic(move)));

        int score = Integer.MAX_VALUE;
        byte bestMove = 0;

        // Check for the best move from the last iteration first
        if (lastBestMoveCache.containsKey(board.zobristHash())) {
            byte move = lastBestMoveCache.get(board.zobristHash()).action;
            if (moves.contains(move)) {
                lastBestMoveCacheHitCount++;
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
            }
        }

        // Perform alpha-beta pruning search on predecessors
        for (byte move : moves) {
            int checkScore = maxValue(moveBoards.get(move), interrupt, a, b, searchingPlayer, depth - 1);
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

        // Cache the search result
        ActionNode result = new ActionNode(score, bestMove);
        lastBestMoveCache.put(board.zobristHash(), result);
        transpositionMap.put(board.zobristHash(), result);

        return score;
    }

    /**
     * Mencari pilihan maksimal dalam permainan dalam sudut pandang pemain bertahan
     * @param board Kondisi terkini papan permainan.
     * @param interrupt Timer untuk penghitungan waktu pencarian solusi.
     * @param a Nilai alfa dari pohon permainan.
     * @param b Nilai beta dari pohon permainan.
     * @param searchingPlayer Player yang sedang melakukan pencarian.
     * @param depth Kedalaman pohon yang dihitung dari pilihan banyaknya ronde permainan.
     * @return result hasil pencarian dengan kedalaman tertentu.
     */
    private static int maxValue(Board board, BooleanSupplier interrupt, int a, int b, PlayerMarks searchingPlayer, int depth) {
        // End search if the maximum depth is reached or this board state is a terminal state
        if (interrupt.getAsBoolean() || depth == 0 || board.isTerminal()) {
            leafCount++;
            return switch (searchingPlayer) {
                case X -> board.getPlayerXScore() - board.getPlayerOScore();
                case O -> board.getPlayerOScore() - board.getPlayerXScore();
                default -> throw new RuntimeException();
            };
        }

        // If this position has been evaluated, return the value from the transposition map
        if (transpositionMap.containsKey(board.zobristHash())) {
            transpositionMapHitCount++;
            return transpositionMap.get(board.zobristHash()).evaluationScore;
        }

        // Generate all predecessors of the current board state
        List<Byte> moves = board.getEmptySquares();
        Map<Byte, Board> moveBoards = generateNextBoardStates(board);
        moves.sort(Comparator.comparingInt(move -> -moveBoards.get(move).heuristic(move)));

        int score = Integer.MIN_VALUE;
        byte bestMove = 0;

        // Check for the best move from the last iteration first
        if (lastBestMoveCache.containsKey(board.zobristHash())) {
            byte move = lastBestMoveCache.get(board.zobristHash()).action;
            if (moves.contains(move)) {
                lastBestMoveCacheHitCount++;
                moves.remove(moves.indexOf(move));
                moves.add(0, move);
            }
        }

        // Perform alpha-beta pruning search on predecessors
        for (byte move : moves) {
            int checkScore = minValue(moveBoards.get(move), interrupt, a, b, searchingPlayer, depth - 1);
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

        // Cache the search result
        ActionNode result = new ActionNode(score, bestMove);
        lastBestMoveCache.put(board.zobristHash(), result);
        transpositionMap.put(board.zobristHash(), result);
        return score;
    }

    /**
     * Menghitung evaluation score dari pohon yang sedang berjalan
     * @param board Kondisi terkini papan permainan.
     * @param tree Pohon action node yang belum dilakukan pencarian nilai terhadapnya.
     * @return Pohon yang telah dievaluasi nilainya.
     */
    public static void evaluateTree(Tree<ActionNode> tree, Board board) {
        evaluateTree(tree, board, board.getCurrentPlayer(), true);
    }

    /**
     * Mencari pilihan minimal dalam permainan dalam sudut pandang pemain lawan
     * @param board Kondisi terkini papan permainan.
     * @param isMax Status apakah evaluasi dilakukan untuk pencarian nilai maksimal atau minimal.
     * @param searchingPlayer Player yang sedang melakukan pencarian.
     * @param tree Pohon action node yang belum dilakukan pencarian nilai terhadapnya.
     * @return Pohon yang telah dievaluasi nilainya.
     */
    private static void evaluateTree(Tree<ActionNode> tree, Board board, PlayerMarks searchingPlayer, boolean isMax) {
        if (tree.getChildren().size() == 0) {
            // This node is terminal: calculate value directly
            Board leafBoard = new Board(board);
            List<Byte> actions = new ArrayList<>();

            // Simulate acting out this path on the board
            Tree<ActionNode> currentTree = tree;
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
                for (Tree<ActionNode> child : tree.getChildren()) {
                    evaluateTree(child, board, searchingPlayer, false);
                    if (tree.getValue().evaluationScore < child.getValue().evaluationScore)
                        tree.getValue().evaluationScore = child.getValue().evaluationScore;
                }
            } else {
                tree.getValue().evaluationScore = Integer.MAX_VALUE;
                for (Tree<ActionNode> child : tree.getChildren()) {
                    evaluateTree(child, board, searchingPlayer, true);
                    if (tree.getValue().evaluationScore > child.getValue().evaluationScore)
                        tree.getValue().evaluationScore = child.getValue().evaluationScore;
                }
            }
        }
    }

    /**
     * Menghasilkan semua papan yang mungkin setelah tindakan pertama yang diberikan pada papan saat ini.
     * @param board Kondisi terkini papan permainan.
     * @return moveBoard Map list semua papan yang mungkin dilakukan pada aksi selanjutnya.
     */
    public static Map<Byte, Board> generateNextBoardStates(Board board) {
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
