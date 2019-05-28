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
        int filePartSize = (int)file.length()/bufferSize;
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

                        String request, response;
                        while (!clientSocket.isClosed() && ((request = in.readLine()) != null)) {
                            response = processRequest(request);
                            out.println(response);
                            if ("Done".equals(request)) {
                                break;
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
