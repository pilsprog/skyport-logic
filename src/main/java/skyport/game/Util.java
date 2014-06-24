package skyport.game;

import java.util.regex.Pattern;

import skyport.exception.ProtocolException;

public class Util {

    public static void validateUsername(String username) throws ProtocolException {
        if (username.length() < 3) {
            throw new ProtocolException("Username too short: needs to be 3 characters or longer.");
        }
        if (username.length() > 16) {
            throw new ProtocolException("Username too long: needs to be 16 characters or less.");
        }
        if (!Pattern.matches("[a-zA-Z0-9-_+]+", username)) {
            throw new ProtocolException("Username contains invalid characters. May only contain " + "a-z, A-Z, 0-9, -, _, +.");
        }
    }
}
