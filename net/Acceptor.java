import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Acceptor {
    private final int port;
    public Acceptor(int port) {
        this.port = port;
    }
    
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap(
              			    new NioServerSocketChannelFactory(
								      Executors.newCachedThreadPool(),
								      Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
		public ChannelPipeline getPipeline() throws Exception {
		    return Channels.pipeline(new AIClientHandler());
		}
	    });
        bootstrap.bind(new InetSocketAddress(port));
    }
}
