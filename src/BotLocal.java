import javax.swing.*;
import java.util.*;

public class BotLocal extends BotBase{
    private static final int k = 30;

    /**
     * Mencari aksi paling optimal yang akan dilakukah bot dengan menerapkan Local Beam Search.
     * @param board Kondisi papan permainan terkini.
     * @return choosenChild Individu anak yang telah dipilih sebagai pemilik aksi paling optimal berdasarkan Local Beam Search.
     */
    @Override
    protected byte searchMove(Board board) {

        /* Initiate */
        // Membangkitkan local tree
        Tree<ActionNode> localTree = new Tree<>(new ActionNode(null, board));
        // Membangkitkan array penyimpanan pohon evaluasi terkini
        List<Tree<ActionNode>> currentEvaluate = new ArrayList<>();
        // Membangkitkan array penyimpanan pohon evaluasi selanjutnya
        List<Tree<ActionNode>> nextEvaluate = new ArrayList<>();
        // Membangkitkan array penyimpanan aksi
        List<Byte> actions = new ArrayList<>();
        // Membangkitkan map list papan aksi
        Map<Byte, Board> boards = new HashMap<>();

        // Assigning variabel terkait yang telah dibangkitkan
        currentEvaluate.add(localTree);
        Tree<ActionNode> currentTree;

        for (int i=0; i<board.getPliesLeft(); i++){
            // Membangkitkan semua pohon evaluasi yang akan dievaluasi
            for (Tree<ActionNode> evaluationTree : currentEvaluate){
                // Menyimpan semua kotak yang tersedia untuk aksi papan evaluasi terkini
                actions = evaluationTree.getValue().board.getEmptySquares();
                // Menghasilkan semua kemungkinan papan berikutnya
                boards = Minimax.generateNextBoardStates(evaluationTree.getValue().board);
                // Memasukkan nilai pohon evaluasi saat ini
                currentTree = evaluationTree;
                // Menambahkan pohon yang dibuat ke list currentTree dan nextEvaluate
                for (Byte action : actions) {
                    Tree<ActionNode> child = new Tree<>(new ActionNode(action, boards.get(action)));
                    currentTree.addChild(child);
                    nextEvaluate.add(child);
                }
            }

            // Mengevaluasi localTree menggunakan algoritma minimax
            Minimax.evaluateTree(localTree, board);

            // Mengurutkan semua nilai nextEvaluate menurut nilai evaluasinya untuk mencari local optimum
            nextEvaluate.sort(Comparator.comparingInt(tree -> -tree.getValue().evaluationScore));
            // Memilih child yang merupakan local optimum
            if (k<nextEvaluate.size()) {
                for (int j = k; j < nextEvaluate.size(); j++) {
                    Tree<ActionNode> removedChild = nextEvaluate.get(j);
                    Tree<ActionNode> parent = removedChild.getParent();
                    if (parent != null) {
                        parent.removeChild(removedChild);
                        while (parent.getParent() != null && parent.getChildren().isEmpty()) {
                            Tree<ActionNode> grandparent = parent.getParent();
                            grandparent.removeChild(parent);
                            parent = grandparent;
                        }
                    }
                }
                nextEvaluate.subList(k, nextEvaluate.size()).clear();
            }

            // Menyimpan konfigurasi terkini
            currentEvaluate.clear();
            currentEvaluate.addAll(nextEvaluate);
            nextEvaluate.clear();
            actions.clear();
            boards.clear();
        }

        // Menilai evaluation score localTree dengan fungsi minimax
        Minimax.evaluateTree(localTree, board);

        // Mengembalikan child dengan nilai paling optimal berdasarkan local beam search
        return localTree.getChild(
                child -> Objects.equals(child.getValue().evaluationScore, localTree.getValue().evaluationScore)
        ).getValue().action;
    }

}
