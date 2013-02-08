import java.util.regex.Pattern;

public class Util {
    public static boolean validateWeapon(String weapon){
	if(weapon.equals("laser") || weapon.equals("mortar") || weapon.equals("droid")){
	    return true;
	}
	return false;
    }
    public static void validateUsername(String username) throws ProtocolException {
	if(username.length() < 3){
	    throw new ProtocolException("Username too short: needs to be 3 characters or longer.");
	}
	if(username.length() > 16){
	    throw new ProtocolException("Username too long: needs to be 16 characters or less.");
	}
	if(!Pattern.matches("[a-zA-Z0-9-_+]+", username)){
	    throw new ProtocolException("Username contains invalid characters. May only contain "
					+ "a-z, A-Z, 0-9, -, _, +.");
	}
    }
}