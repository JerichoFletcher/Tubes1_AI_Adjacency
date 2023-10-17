public class Individual {
    public Byte[] actions;
    public Tree<ReservationNode> leaf;
    public Integer fitnessValue;

    public Individual(Byte[] actions, Tree<ReservationNode> leaf){
        this.actions = new Byte[actions.length];
        System.arraycopy(actions, 0, this.actions, 0, actions.length);
        this.leaf = leaf;
        this.fitnessValue = null;
    }

    public void setAction(int idx, Byte value){
        this.actions[idx] = value;
    }

    public void calcFitnessValue(){
        // Fitness value = banyaknya level dia bisa naik
        this.fitnessValue = 0;
        Tree<ReservationNode> currentTree = this.leaf;
        do {
            if (currentTree.getValue() == currentTree.getParent().getValue()){
                this.fitnessValue++;
                currentTree = currentTree.getParent();
            } else {
                break;
            }
        } while (currentTree.hasParent());
        this.fitnessValue *= this.fitnessValue;
    }
}
