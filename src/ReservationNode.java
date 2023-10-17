import java.lang.Override;

public class ReservationNode {
    public Integer evaluationScore;
    public Byte action;

    public ReservationNode(Integer evaluationScore, Byte action){
        this.evaluationScore = evaluationScore;
        this.action = action;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof ReservationNode node
                && this.evaluationScore == node.evaluationScore
                && this.action == node.action;
    }
}
