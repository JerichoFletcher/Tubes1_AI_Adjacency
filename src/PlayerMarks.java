public enum PlayerMarks {
    EMPTY,
    X,
    O;

    @Override
    public String toString() {
        return switch(this){
            case EMPTY -> "";
            case X -> "X";
            case O -> "O";
        };
    }
}
