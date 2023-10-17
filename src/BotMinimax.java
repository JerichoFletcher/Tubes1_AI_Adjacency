public class BotMinimax extends BotBase {
    @Override
    protected byte searchMove(Board board) {
        return Minimax.findOne(board, this::isStopped);
    }
}
