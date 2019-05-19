package org.romanchi.client;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Client extends Thread implements AutoCloseable {

    private final static Logger logger = Logger.getLogger(Client.class.getName());

    @Getter @Setter private FileDescriptor fileToDownload;
    private List<FilePart> fileParts;
    private File downloadedFile;
    private FileOutputStream fileOutputStream;
    private Socket socket;

    @Getter private String host = "localhost";
    @Getter @Setter private int port = 8888;
    @Getter @Setter private int filePartSize = 65000;

    public Client(){
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        try {
            socket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileToDownload(FileDescriptor fileToDownload) throws FileNotFoundException {
        this.fileToDownload = fileToDownload;
        int partsAmount = (int) (fileToDownload.getFileSize()/filePartSize);
        logger.info("PARTS AMOUNT: " + partsAmount);
        fileParts = new ArrayList(partsAmount);
        for(int i = 0; i < partsAmount; i++){
            fileParts.add(FilePart.builder().id(i).build());
        }
        downloadedFile = new File("downloadedFile.png");
        fileOutputStream = new FileOutputStream(downloadedFile);
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
            read();
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {interrupt();}*/
        }
        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("org.romanchi.org.romanchi.client.Client has been closed");
    }

    public void read(){
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[filePartSize + 100];
            int dataLength = inputStream.read(buffer);
            String data = new String(buffer, 0, dataLength);
            logger.info("CLIENT READ: " + data);
            Integer index = Integer.valueOf(data.substring(0, data.indexOf("@")));
            fileOutputStream.write(buffer, index, dataLength - index);
            fileOutputStream.flush();
            fileParts.get(index).setPresent(true);
            logger.info("INDEX: " + index);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
