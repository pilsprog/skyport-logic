package skyport.game;

public class Point {
    public int j = 0;
    public int k = 0;

    public Point(int j, int k) {
        this.j = j;
        this.k = k;
    }

    public Point(String commaDelimitedVectorString) {
        String[] tokens = commaDelimitedVectorString.split(",");
        j = Integer.parseInt(tokens[0]);
        k = Integer.parseInt(tokens[1]);
    }

    public String getString() {
        return "[" + j + ", " + k + "]";
    }

    public String getCompactString() {
        return j + ", " + k;
    }
}
