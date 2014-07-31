package skyport.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import skyport.exception.ProtocolException;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.message.HandshakeMessage;
import skyport.message.LoadoutMessage;
import skyport.network.Connection;

public class Utils {
    
    public static Connection getMockConnection(String name) throws ProtocolException {
        HandshakeMessage handshake = spy(new HandshakeMessage());
        doNothing()
            .when(handshake)
            .validate();
        when(handshake.getName())
            .thenReturn(name);
        
        LoadoutMessage loadout = spy(new LoadoutMessage());
        doNothing()
            .when(loadout)
            .validate();
        doReturn(new Droid())
            .when(loadout)
            .getPrimaryWeapon();
        doReturn(new Laser())
            .when(loadout)
            .getSecondaryWeapon();
        
        Connection conn = mock(Connection.class);
        when(conn.next())
            .thenReturn(handshake)
            .thenReturn(loadout);

        return conn;
    }

}
