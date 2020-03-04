package cn.sanleny.jt808.server.framework.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * @Author: lg
 * @Date: 2019/10/30
 * @Version: 1.0
 **/
@Slf4j
@Component
public class Jt808TcpServer {

    @Value("${jt808.bind_address}")
    private String host;
    @Value("${jt808.bind_port}")
    private Integer port;

    @Value("${jt808.netty.leak_detector_level}")
    private String leakDetectorLevel;
    @Value("${jt808.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;
    @Value("${jt808.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;

    private Channel serverChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Autowired
    private Jt808ChannelInitializer jt808ChannelInitializer;

    /**
     * 初始化server
     */
    @PostConstruct
    public void init() throws InterruptedException {

        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        //内存泄漏检测 开发推荐 PARANOID 线上使用默认的SIMPLE即可
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));

        log.info("Starting jt808 transport server");
        //创建EventLoopGroup
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        //创建启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 配置启动器
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)  //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                .childOption(ChannelOption.SO_KEEPALIVE, true) //长连接
                .childHandler(jt808ChannelInitializer);
        serverChannel = serverBootstrap.bind(host, port).sync().channel();
        log.info("jt808 transport started ... port:{}",port);
    }

    /**
     * 销毁资源
     * @throws InterruptedException
     */
    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping jt808 transport!");
        try {
            serverChannel.close().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("jt808 transport stopped!");
    }
}
