import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.logging.Logger;


public class Client extends Thread implements AutoCloseable {

    private final static Logger logger = Logger.getLogger(Client.class.getName());

    private Socket socket;

    @Getter @Setter private String host = "localhost";
    @Getter @Setter private int port = 8888;

    public Client(){
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        try {
            socket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        logger.info("Client has bean started");
        while(!isInterrupted()){
            send(UUID.randomUUID().toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {interrupt();}
        }
        logger.info("Client has been closed");
    }

    public void send(String data) {
        try {
            logger.info("CLIENT SENDED: " + data);
            OutputStream os = socket.getOutputStream();
            os.write(data.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if(socket != null && !socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
