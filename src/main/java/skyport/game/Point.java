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

    public Point pluss(Point v) {
        return new Point(this.j+v.j, this.k+v.k);
    }
    
    @Override
    public String toString() {
        return "[" + j + "," + k +"]";
    }
}
