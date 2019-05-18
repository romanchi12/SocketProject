package org.romanchi.client;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Client extends Thread implements AutoCloseable {

    private final static Logger logger = Logger.getLogger(Client.class.getName());

    @Getter @Setter private FileDescriptor fileToDownload;
    private List<FilePart> fileParts;
    private File downloadedFile;
    private Socket socket;

    @Getter private String host = "localhost";
    @Getter @Setter private int port = 8888;
    @Getter @Setter private long filePartSize = 1024;

    public Client(){
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        try {
            socket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileToDownload(FileDescriptor fileToDownload) {
        this.fileToDownload = fileToDownload;
        int partsAmount = (int) (fileToDownload.getFileSize()/filePartSize);
        fileParts = new ArrayList(partsAmount);
        downloadedFile = new File(fileToDownload.getFileName());
    }

    private String getRequest(){
        if(fileParts != null){
            return fileParts.stream()
                    .filter(filePart -> !filePart.isPresent())
                    .map(filePart -> String.valueOf(filePart.getId()))
                    .collect(Collectors.joining(","));
        }
        return "";
    }

    @Override
    public void run(){
        logger.info("org.romanchi.org.romanchi.client.Client has bean started");
        while(!isInterrupted()){
            send(getRequest());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {interrupt();}
        }
        logger.info("org.romanchi.org.romanchi.client.Client has been closed");
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
