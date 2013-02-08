public class Coordinate {
    public int j = 0;
    public int k = 0;
    public Coordinate(int jArg, int kArg){
	j = jArg;
	k = kArg;
    }
    public String getString(){
	return "[" + j + ", " + k + "]";
    }
}
