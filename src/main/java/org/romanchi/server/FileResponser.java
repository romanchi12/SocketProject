package org.romanchi.server;

import lombok.Getter;
import lombok.Setter;
import org.romanchi.client.FilePart;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileResponser implements Responser {
    private final static Logger logger = Logger.getLogger(Responser.class.getName());

    private File file = new File("D:\\завантаження\\фон.png");
    private DataInputStream dataInputStream;
    private Socket clientSocket;

    @Getter
    @Setter
    private int filePartSize = 65000;


    public FileResponser(Socket clientSocket){
        this.clientSocket = clientSocket;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        List<FilePart> fileParts = null;
        while(!Thread.currentThread().isInterrupted()){
            fileParts = read();
            if (fileParts != null){
                sendPart(fileParts.get(0));
            }
        }
    }

    public void sendPart(FilePart filePart){
        try {
            OutputStream os = clientSocket.getOutputStream();
            byte[] prefix = (filePart.getId() + "@").getBytes();
            byte[] buffer = new byte[filePartSize + prefix.length];
            int readedBytes = dataInputStream.read(buffer, 0, filePartSize);
            if(readedBytes < 0){
                logger.info("TRANSACTION ENDED.");
                return;
            }
            System.arraycopy(prefix, 0, buffer, 0, prefix.length);
            os.write(buffer, 0, readedBytes + prefix.length);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<FilePart> read(){
        try {
            InputStream inputStream = clientSocket.getInputStream();
            byte [] buffer = new byte[1024*1024];
            String data = "";
            int dataLength = inputStream.read(buffer);
            data = data.concat(new String(buffer, 0, dataLength));
            logger.info("SERVER READ: " + data);
            if("".equals(data)){
                logger.info("FILE HAS BEEN DOWNLOADED");
            }
            List<FilePart> fileParts = Arrays.stream(data.split(","))
                    .map(Integer::valueOf)
                    .map(id -> FilePart.builder().id(id).build())
                    .collect(Collectors.toList());
            logger.info("DATA LENGTH: " + dataLength);
            return fileParts;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void send(Object object) {

    }
}
