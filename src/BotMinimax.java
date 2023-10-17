public class BotMinimax extends BotBase {
    @Override
    protected byte searchMove(Board board) {
        return Minimax.startSearch(board, this::isStopped, board.getPliesLeft());
    }
}
