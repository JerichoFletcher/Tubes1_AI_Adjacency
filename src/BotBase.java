public abstract class BotBase {
    private boolean stopped = false;

    /**
     * Menghentikan pencarian oleh bot.
     */
    public final synchronized void stop() {
        this.stopped = true;
    }

    /**
     * Mengembalikan apakah pencarian bot sudah dihentikan.
     * @return {@code true} jika pencarian bot sudah dihentikan.
     */
    public final boolean isStopped() {
        return this.stopped;
    }

    /**
     * Memulai pencarian langkah oleh bot, dimulai dari suatu keadaan papan permainan yang diberikan.
     * @param board Keadaan papan saat ini, yang digunakan sebagai titik mula pencarian.
     * @return Koordinat langkah yang ditemukan.
     */
    public final int[] move(Board board) {
        this.stopped = false;
        byte result = searchMove(board);
        return new int[]{Coordinate.getX(result), Coordinate.getY(result)};
    }

    protected abstract byte searchMove(Board board);
}
