package skyport.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import skyport.exception.ProtocolException;
import skyport.message.HandshakeMessage;
import skyport.network.Connection;

public class Utils {
    
    public static Connection getMockConnection(String name) throws ProtocolException {
        HandshakeMessage message = spy(new HandshakeMessage());
        doNothing()
            .when(message)
            .validate();
        when(message.getName())
            .thenReturn(name);
        
        Connection conn = mock(Connection.class);
        when(conn.next())
            .thenReturn(message);

        return conn;
    }

}
