import java.lang.Override;
import java.util.Objects;

// Action Node Class
public class ActionNode {
    public Integer evaluationScore;
    public Byte action;
    public Board board;

    public ActionNode(){
        this.evaluationScore = null;
        this.action = null;
        this.board = null;
    }

    public ActionNode(Integer evaluationScore, Byte action){
        this.evaluationScore = evaluationScore;
        this.action = action;
        this.board = null;
    }

    public ActionNode(Byte action, Board board){
        this.evaluationScore = null;
        this.action = action;
        this.board = board;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof ActionNode node
//                && Objects.equals(this.evaluationScore, node.evaluationScore)
                && Objects.equals(this.action, node.action);
    }
}
