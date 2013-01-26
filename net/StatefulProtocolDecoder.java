import org.json.*;

public class StatefulProtocolDecoder {
    int supportedProtocolVersion = 1;
    int id = 0;
    boolean hasHandshake = false;
    public StatefulProtocolDecoder(){
	hasHandshake = false;
    }
    public String parseLine(String line) throws ProtocolException {
	try {
	    JSONObject obj = new JSONObject(line);
	    try {
		int version = (int)obj.get("connect");
		if(version != supportedProtocolVersion){
		    throw new ProtocolException("Wrong protocol version: expected " + supportedProtocolVersion + ", got " + version);
		}
		else {
		    hasHandshake = true;
		    String jsonReturnMessage = null;
		    JSONStringer stringer = new JSONStringer();
		    try {
			jsonReturnMessage = stringer.object().key("connect").value(true).endObject().toString();
		    }
		    catch (JSONException e){}
		    return jsonReturnMessage;
		}
	    }
	    catch (JSONException e){
		if(!hasHandshake){
		    throw new ProtocolException("You need to send a hanshake first");
		}
		return parseNonHandshake(obj);
	    }
	}
	catch (JSONException e) {
	    throw new ProtocolException(e.toString());
	}
    }
    private String parseNonHandshake(JSONObject obj){
	
	return null;
    }
}
