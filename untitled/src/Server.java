import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务器
 *
 * @author TONY
 *
 */
public class Server {
    // 缓冲区的大小
    private final static int BUFFER_SIZE = 1024;

    // 缓冲区
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    // Server监听的端口号
    // private final static int PORT = 8888;

    // 选择器
    private Selector selector = null;

    // 初始化工作
    public void init(int port) throws IOException {
        System.out.println("============ Listening On Port : " + port + "============");
        // 打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞状态
        serverSocketChannel.configureBlocking(false);
        // 获取通道相关联的套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 绑定端口号
        serverSocket.bind(new InetSocketAddress(port));
        // 打开一个选择器
        selector = Selector.open();
        // 服务器套接字注册到Selector中 并指定Selector监控连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen() throws IOException {
        while (true) {
            // 开启选择
            int readyChannels = selector.select(); // 没有通道就绪 一直阻塞 返回已经就绪通道的数目(有可能为0)
            if (readyChannels == 0) {
                continue;
            }
            // 返回已选择键的集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            // 遍历键 并检查键对应的通道里注册的就绪事件
            Iterator iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                // SelectionKey封装了一个通道和选择器的注册关系
                SelectionKey key = (SelectionKey) iterator.next();
                handleKey(key);
                // Selector不会移除SelectionKey 处理完了手动移除
                iterator.remove();
            }
        }
    }

    // 处理SelectionKey
    private void handleKey(SelectionKey key) throws IOException {
        // 是否有连接进来
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();// 获取通道 转化为要处理的类型
            SocketChannel socketChannel = server.accept();
            // SocketChannel通道的可读事件注册到Selector中
            registerChannel(selector, socketChannel, SelectionKey.OP_READ);
            // 连接成功 向Client打个招呼
            if (socketChannel.isConnected()) {
                buffer.clear();
                buffer.put("I am Server...".getBytes());
                buffer.flip();
                socketChannel.write(buffer);

            }

        }
        // 通道的可读事件就绪
        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear(); // 清空缓冲区
            // 读取数据
            int len = 0;
            while ((len = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.println("Server读取的数据:" + new String(buffer.array(), 0, len));
                }
            }
            if (len < 0) {
                // 非法的SelectionKey 关闭Channel
                socketChannel.close();
            }
            // SocketChannel通道的可写事件注册到Selector中
            registerChannel(selector, socketChannel, SelectionKey.OP_WRITE);
        }
        // 通道的可写事件就绪
        if (key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear(); // 清空缓冲区
            // 准备发送的数据
            String message_from_server = "Hello,Client... " + socketChannel.getLocalAddress();
            buffer.put(message_from_server.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            System.out.println("Server发送的数据:" + message_from_server);
            // SocketChannel通道的可写事件注册到Selector中
            registerChannel(selector, socketChannel, SelectionKey.OP_READ);
        }

    }

    // 注册通道到指定Selector上
    private void registerChannel(Selector selector, SelectableChannel channel, int ops) throws IOException {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        // 注册通道
        channel.register(selector, ops);

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init(8888);
        server.listen();
    }
}
