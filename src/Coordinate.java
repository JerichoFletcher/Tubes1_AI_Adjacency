public class Coordinate {
    // xxxxyyyy
    public static byte getX(byte coordinate) {
        return (byte) ((coordinate & 0xF0) >> 4);
    }

    public static byte getY(byte coordinate) {
        return (byte) (coordinate & 0xF);
    }

    public static byte of(byte x, byte y) {
        return (byte) (((x & 0xF) << 4) | (y & 0xF));
    }

    public static String toString(byte coordinate) {
        return String.format("(%s, %s)", getX(coordinate), getY(coordinate));
    }
}
