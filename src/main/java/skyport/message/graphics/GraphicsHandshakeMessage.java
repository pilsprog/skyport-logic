package skyport.message.graphics;

import skyport.message.HandshakeMessage;

public class GraphicsHandshakeMessage extends HandshakeMessage {
    private String password;
    private String laserstyle;

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public String getLaserStyle() {
        return laserstyle;
    }
}
