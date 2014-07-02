package skyport.game;

public class Vector {
    public final int j;
    public final int k;

    public Vector(int j, int k) {
        this.j = j;
        this.k = k;
    }

    public String getString() {
        return "[" + j + ", " + k + "]";
    }

    public Vector plus(Vector v) {
        return new Vector(this.j+v.j, this.k+v.k);
    }
    
    @Override
    public String toString() {
        return "[" + j + "," + k +"]";
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Vector) {
            Vector v = (Vector)o;
            return this.j == v.j && this.k == v.k;
        }
        return false;
    }
}
