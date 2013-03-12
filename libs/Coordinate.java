public class Coordinate {
    public int j = 0;
    public int k = 0;
    public Coordinate(int jArg, int kArg){
	j = jArg;
	k = kArg;
    }
    public Coordinate(String commaDelimitedVectorString){
	String[] tokens = commaDelimitedVectorString.split(",");
	j = Integer.parseInt(tokens[0]);
	k = Integer.parseInt(tokens[1]);
    }
    public String getString(){
	return "[" + j + ", " + k + "]";
    }
    public String getCompactString(){
	return j + ", " + k;
    }
}
