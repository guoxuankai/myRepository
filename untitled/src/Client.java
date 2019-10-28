
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端
 *
 * @author TONY
 */
public class Client {

    // 缓冲区的大小
    private final static int BUFFER_SIZE = 1024;
    // 缓冲区
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    // 选择器
    private Selector selector = null;

    private final static int PORT = 8888;

    // 初始化工作
    public void init(String address) throws IOException {

        // 打开客户端套接字通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞状态
        socketChannel.configureBlocking(false);
        // 打开选择器
        selector = Selector.open();
        // 注册
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        // 发起连接
        socketChannel.connect(new InetSocketAddress(address, PORT));

    }

    public void connect() throws IOException {
        while (true) {
            int readyChannels = selector.select();
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
        // 是否可连接
        if (key.isConnectable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            // 完成连接
            if (socketChannel.isConnectionPending()) {
                socketChannel.finishConnect();
                System.out.println("连接成功...");
                // 发送数据给Server
                String message_to_server = "Hello,Server...";
                buffer.clear();
                buffer.put(message_to_server.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                System.out.println("Client发送的数据:" + message_to_server);
                registerChannel(selector, socketChannel, SelectionKey.OP_READ);
            } else {
                System.exit(1); // 连接失败 退出
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
                    System.out.println("Client读取的数据:" + new String(buffer.array(), 0, len));
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
            String message_from_server = "Hello,Server... " + socketChannel.getLocalAddress();
            buffer.put(message_from_server.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            System.out.println("Client发送的数据:" + message_from_server);
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
        Client client = new Client();
        client.init("localhost");
        client.connect();
    }

}