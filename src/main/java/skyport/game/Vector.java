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

    public Vector pluss(Vector v) {
        return new Vector(this.j+v.j, this.k+v.k);
    }
    
    @Override
    public String toString() {
        return "[" + j + "," + k +"]";
    }
}
