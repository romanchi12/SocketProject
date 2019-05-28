package org.romanchi.server;

import lombok.Getter;
import lombok.Setter;
import org.romanchi.client.FilePart;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;


public class HttpResponser implements Responser {

    private final static Logger logger = Logger.getLogger(Responser.class.getName());

    private Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    @Getter
    @Setter
    private int filePartSize = 65000;


    public HttpResponser(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        logger.info(clientSocket.getInetAddress().getHostAddress() + " " + clientSocket.getPort());
    }

    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted() || !clientSocket.isConnected()){
            String data;
            try {
                data = read();
                send(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPart(FilePart filePart){
    }

    public String read() throws IOException {
        if(clientSocket.getInputStream().available() <= 0) {
            return "";
        }
        byte[] buffer = new byte[1024];
        StringBuilder data = new StringBuilder();
        int readed = 0;
        int acumulated = readed;
        while((readed = clientSocket.getInputStream().read(buffer)) > 0){
            logger.info("SERVER READ PART: " + new String(buffer, 0, readed));
            data.append(new String(buffer, 0, readed));
            acumulated += readed;
            if(clientSocket.getInputStream().available() > acumulated || clientSocket.getInputStream().available() <= 0){
                break;
            }
        }
        logger.info("SERVER READ: " + data.toString());

        return data.toString();
    }

    @Override
    public void send(Object object) {

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "\r\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "<title>Top 20+ MySQL Best Practices - Nettuts+</title>\n" +
                "</head>\n" +
                "<body>" + object.toString() + "</body>\n" +
                "</html>";
        if(clientSocket.isConnected()){
            logger.info("SERVER SEND: " + response);
            out.write(response);
            out.flush();
        }
    }
}
