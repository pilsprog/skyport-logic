package skyport.game;

public class Vector2d {
    public final int j;
    public final int k;

    public Vector2d(int j, int k) {
        this.j = j;
        this.k = k;
    }

    public String getString() {
        return "[" + j + ", " + k + "]";
    }

    public Vector2d plus(Vector2d v) {
        return new Vector2d(this.j+v.j, this.k+v.k);
    }
    
    @Override
    public String toString() {
        return "[" + j + "," + k +"]";
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Vector2d) {
            Vector2d v = (Vector2d)o;
            return this.j == v.j && this.k == v.k;
        }
        return false;
    }
}
