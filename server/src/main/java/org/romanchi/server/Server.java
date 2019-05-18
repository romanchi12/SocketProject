package org.romanchi.server;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class Server extends Thread implements AutoCloseable{

    private final static Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocket socket;
    private List<Responser> clients;

    @Getter @Setter private String host = "localhost";
    @Getter @Setter private int port = 8888;

    public Server() {
        boolean isCreated = false;
        while(!isCreated){
            try {
                init();
                isCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
                port++;
                isCreated = false;
            }
        }
    }

    private void init() throws IOException{
        clients = new LinkedList<>();
        socket = new ServerSocket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        socket.bind(socketAddress);
    }

    @Override
    public void run(){
        logger.info("org.romanchi.server.Server has been started");
        while (!isInterrupted()){
            try {
                logger.info("Waiting for connection");
                Responser responser = new Responser(socket.accept());
                clients.add(responser);
                responser.start();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Connection establishing failed");
            }
        }
        close();
        logger.info("org.romanchi.server.Server has been closed");
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
