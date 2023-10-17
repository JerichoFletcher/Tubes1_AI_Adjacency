import java.lang.Override;
import java.util.Objects;

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
//                && Objects.equals(this.evaluationScore, node.evaluationScore)
                && Objects.equals(this.action, node.action);
    }
}
