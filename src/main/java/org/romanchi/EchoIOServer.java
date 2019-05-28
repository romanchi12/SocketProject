package org.romanchi;

import org.romanchi.client.FilePart;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class EchoIOServer {


    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(("C:\\Users\\Roman\\IdeaProjects\\ыщслуе\\src\\main\\resources\\app.properties")));
        String hostName = properties.getProperty("server.host");
        int portNumber = Integer.valueOf(properties.getProperty("server.port"));
        String fileName = properties.getProperty("server.file.path");
        int bufferSize = Integer.valueOf(properties.getProperty("server.buffer.size"));
        File file = new File(fileName);
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        System.out.println("[server] file length " + file.length());
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(hostName, portNumber));
            System.out.println("Waiting connections on " + hostName + ":" + portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected client " + clientSocket.getLocalAddress().getHostName() + ":" + clientSocket.getPort());
                new Thread(() -> {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));

                        String request;
                        while (!clientSocket.isClosed() && ((request = in.readLine()) != null)) {
                            if(request.equals("get")){
                                byte [] buffer = new byte[bufferSize];
                                int readed;
                                int acumulated = 0;
                                out.write("length " + file.length());
                                out.flush();
                                while ((readed = dataInputStream.read(buffer)) > 0){
                                    clientSocket.getOutputStream().write(buffer, 0, readed);
                                    acumulated += readed;
                                    System.out.println("[server] Uploading .... " + ((float)(acumulated)/file.length()) * 100 + "%");
                                }
                                clientSocket.getOutputStream().flush();
                                clientSocket.getOutputStream().write("password".getBytes());
                                clientSocket.getOutputStream().flush();
                            }else{
                                System.out.println("No get command");
                            }
                        }
                        if(!clientSocket.isClosed()){
                            clientSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

    }

    public static String processRequest(String request) {
        System.out.println("Server receive message from > " + request);
        return request;
    }

}
