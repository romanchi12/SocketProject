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

    @Getter private String host = "77.47.205.79";
    @Getter @Setter private int port = 8888;
    @Getter @Setter private int filePartSize = 65000;
    PrintWriter out;
    BufferedReader in;

    public Client() throws IOException {
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        try {
            socket.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        return "Hello serverok";
    }

    @Override
    public void run(){
        logger.info("org.romanchi.org.romanchi.client.Client has bean started");
        while(!isInterrupted()){
            send(getRequest());
            String data = null;
            try {
                data = read();
                logger.info("CLIENT READ: " + data);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        logger.info("org.romanchi.org.romanchi.client.Client has been closed");
    }

    private String read() throws IOException {
        String request, response;
        while ((request = in.readLine()) != null){
            logger.info("request: " + request);
        }
        return "werw";
    }

    public void send(String data) {
        logger.info("CLIENT SENDED: " + data);
        out.write(data);
        out.flush();
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
