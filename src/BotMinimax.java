public class BotMinimax extends BotBase {
    @Override
    /**
     * Mencari aksi paling optimal yang akan dilakukah bot dengan menerapkan Minimax Aplha-beta Prunning.
     * @param board Kondisi papan permainan terkini.
     * @return choosenChild Individu anak yang telah dipilih sebagai pemilik aksi paling optimal berdasarkan Genetic Algorithm.
     */
    protected byte searchMove(Board board) {
        return Minimax.startSearch(board, this::isStopped, board.getPliesLeft());
    }
}
