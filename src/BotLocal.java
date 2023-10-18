import javax.swing.*;
import java.util.*;

public class BotLocal extends BotBase{
    private static final int k = 500;
    @Override
    protected byte searchMove(Board board) {
        // local beam search

        // initiate
        Tree<ActionNode> localTree = new Tree<>(new ActionNode(null, board));
        List<Tree<ActionNode>> currentEvaluate = new ArrayList<>();
        List<Tree<ActionNode>> nextEvaluate = new ArrayList<>();
        List<Byte> actions = new ArrayList<>();
        Map<Byte, Board> boards = new HashMap<>();

        // assign initiate vars
        currentEvaluate.add(localTree);
        Tree<ActionNode> currentTree;

        for (int i=0; i<board.getPliesLeft(); i++){
//            System.out.printf("Ply %d\n", i);
            // if has reached the leaf, break
//            if (currentEvaluate.get(0).getValue().board.isTerminal()){
//                break;
//            }
            if (isStopped()){
                System.out.println("break");
                break;
            }
            for (Tree<ActionNode> evaluationTree : currentEvaluate){
                actions = evaluationTree.getValue().board.getEmptySquares();
                boards = Minimax.generateNextBoardStates(evaluationTree.getValue().board);
                currentTree = evaluationTree;
                for (Byte action : actions) {
                    Tree<ActionNode> child = new Tree<>(new ActionNode(action, boards.get(action)));
                    currentTree.addChild(child);
                    nextEvaluate.add(child);
                }
            }

            Minimax.evaluateTree(localTree, board);
            nextEvaluate.sort(Comparator.comparingInt(tree -> -tree.getValue().evaluationScore));
//            nextEvaluate.sort(Comparator.comparingInt(tree -> -tree.getParent().getValue().board.heuristic(tree.getValue().action))); // sort by heuristic
//            System.out.println("(action, parent)");
//            nextEvaluate.forEach(t -> System.out.printf("(%d, %d)", t.getValue().action, t.getParent().getValue().action));
            if (k<nextEvaluate.size()) {
                for (int j = k; j < nextEvaluate.size(); j++) {
                    Tree<ActionNode> removedChild = nextEvaluate.get(j);
//                    System.out.printf("Removed child: (%d, parent %d)\n", removedChild.getValue().action, removedChild.getParent().getValue().action);

                    Tree<ActionNode> parent = removedChild.getParent();
                    if (parent != null) {
                        parent.removeChild(removedChild);
                        while (parent.getParent() != null && parent.getChildren().isEmpty()) {
                            Tree<ActionNode> grandparent = parent.getParent();
                            grandparent.removeChild(parent);
                            parent = grandparent;
                        }
                    }

//                    if (i>0){
//                        currentTree = currentTree.getParent();
////                        printTree(currentTree,2);
//                        currentTree = currentTree.getChild(child -> Objects.equals(child, removedChild.getParent()));
//                    }
//                    currentTree.removeChild(removedChild); // remove all child from k to last
                }
                nextEvaluate.subList(k, nextEvaluate.size()).clear(); // remove all child tree from k to last (keep only k child trees)

            }

//            System.out.println("Local Tree");
//            printTree(localTree, 2);

            // next
            currentEvaluate.clear();
            currentEvaluate.addAll(nextEvaluate);
            nextEvaluate.clear();
            actions.clear();
            boards.clear();
        }

        // clean up tree from nodes with no children
//        printTree(localTree, 1);
//        treeCleanUp(board, localTree);

        // evaluate localTree with minimax
        Minimax.evaluateTree(localTree, board);
//        System.out.println("FINAL TREE");
//        printTree(localTree, 2);

        return localTree.getChild(
                child -> Objects.equals(child.getValue().evaluationScore, localTree.getValue().evaluationScore)
        ).getValue().action;
    }

//    private void treeCleanUp(Board board, Tree<ActionNode> localTree){
//        for (Tree<ActionNode> child : localTree.getChildren()) {
//            if (child.getChildren() != null){
//                // if hasn't reached the leaf
//                if (child.getValue().board.isTerminal()){
//                    // if node has no children, remove the node
//                    child.getParent().removeChild(child);
//                } else {
//                    treeCleanUp(board, child);
//                }
//            }
//        }
//    }
    private void printTree(Tree<ActionNode> tree, int indent) {
        for (int i = 0; i < indent; i++) System.out.print("  ");
        if (tree.getValue().action == null) System.out.printf("null: %s\n", tree.getValue().evaluationScore);
        else System.out.printf("(%s, %s): %s\n", Coordinate.getX(tree.getValue().action), Coordinate.getY(tree.getValue().action), tree.getValue().evaluationScore);
        for (Tree<ActionNode> child : tree.getChildren()) printTree(child, indent + 1);
    }
}
