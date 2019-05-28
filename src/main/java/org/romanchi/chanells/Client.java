package org.romanchi.chanells;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class Client extends Thread{

    private final static Logger logger = Logger.getLogger(Client.class.getCanonicalName());
    private String host;
    private int port;
    private SocketChannel socketChannel;

    public Client() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(("C:\\Users\\Roman\\IdeaProjects\\ыщслуе\\src\\main\\resources\\app.properties")));
        host = properties.getProperty("server.host");
        port = Integer.valueOf(properties.getProperty("server.port"));
    }

    public void startClient() throws IOException {
        logger.info("CLIENT STARTING");
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        logger.info("CLIENT STARTED");
    }
    @Override
    public void run(){
        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true){
            write();
            read();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("Hello server!".getBytes());
        byteBuffer.flip();
        logger.info("CLIENT WRITING");
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("CLIENT WRITED");
    }
    public void read(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        logger.info("CLIENT READING");
        try {
            socketChannel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("CLIENT READED: " + Arrays.toString(byteBuffer.array()));

    }

}
