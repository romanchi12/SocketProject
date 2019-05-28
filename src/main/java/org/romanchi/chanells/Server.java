package org.romanchi.chanells;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class Server extends Thread {

    private final static Logger logger = Logger.getLogger(Server.class.getCanonicalName());


    private String host;
    private int port;
    private SocketChannel socketChannel;
    private Selector selector;

    public Server() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(("C:\\Users\\Roman\\IdeaProjects\\ыщслуе\\src\\main\\resources\\app.properties")));
        host = properties.getProperty("server.host");
        port = Integer.valueOf(properties.getProperty("server.port"));
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            int readyCount = 0;
            try {
                readyCount = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (readyCount == 0) {
                continue;
            }
            logger.info("READY COUNT: " + readyCount);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel client;
                    try {
                        client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (selectionKey.isReadable()) {
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    int BUFFER_SIZE = 1024;
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    readRequest(buffer);
                    try {
                        client.register(selector, SelectionKey.OP_WRITE);
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }

                    try {
                        client.read(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                if(selectionKey.isWritable()){
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    int BUFFER_SIZE = 1024;
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    try {
                        writeResponse(buffer);
                        client.register(selector, SelectionKey.OP_READ);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        client.read(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void startServer () throws IOException {
        logger.info("SERVER STARTING");
        selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void readRequest (ByteBuffer byteBuffer) {
        logger.info("READING");
        logger.info("READED: [" + Arrays.toString(byteBuffer.array()) + "]" + "." +
                "\n PREPARING RESPONSE");

    }
    private void writeResponse(ByteBuffer byteBuffer) throws IOException {
        logger.info("RESPOSE: [" + Arrays.toString(byteBuffer.array()) + "]");
        socketChannel.write(ByteBuffer.wrap(byteBuffer.array()));
        logger.info("SERVER RESPONSE");
    }
}