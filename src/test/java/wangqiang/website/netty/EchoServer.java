package wangqiang.website.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by wangq on 2017/5/19.
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println("Usage:" + EchoServer.class.getName() + "<port>");
        }

        int port = Integer.parseInt(args[0]);

        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class).group(eventExecutors).localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(echoServerHandler);
            }
        });

        try {
            ChannelFuture sync = bootstrap.bind().sync();//等待异步绑定完成
            sync.channel().closeFuture().sync();// 阻塞当前线程直到完成
        } finally {
            eventExecutors.shutdownGracefully().sync();//释放所有资源
        }
    }
}
