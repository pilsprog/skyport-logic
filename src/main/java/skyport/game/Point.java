package skyport.game;

public class Point {
    public final int j;
    public final int k;

    public Point(int j, int k) {
        this.j = j;
        this.k = k;
    }

    public String getString() {
        return "[" + j + ", " + k + "]";
    }
}
