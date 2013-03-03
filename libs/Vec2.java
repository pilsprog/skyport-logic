public class Vec2 {
    public int j = 0;
    public int k = 0;
    void parseString(String string){
	String[] elems = string.split(",");
	j = Integer.parseInt(elems[0]);
	k = Integer.parseInt(elems[1]);
    }
    String str(){
	return j + ", " + k;
    }
}
