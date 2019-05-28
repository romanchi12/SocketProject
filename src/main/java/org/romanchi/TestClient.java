package org.romanchi;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;


public class TestClient {
    public static void main(String[] args) {

        Runnable client = () -> {
            try {
                new TestClient().startClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();
    }



    private void startClient() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(("C:\\Users\\Roman\\IdeaProjects\\ыщслуе\\src\\main\\resources\\app.properties")));
        String hostName = properties.getProperty("server.host");
        int portNumber = Integer.valueOf(properties.getProperty("server.port"));
        String threadName = Thread.currentThread().getName();
        String[] messages = new String[] { threadName + " > msg1", threadName + " > msg2", threadName + " > msg3", threadName +
                " > Done" };

        try {
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            for (String message : messages) {
                Scanner stdIn = new Scanner(System.in);
                String userInput;
                while ((userInput = stdIn.next()) != null) {
                    out.println(userInput);
                    System.out.println("echo: " + in.readLine());
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + ".." + e.toString());
            System.exit(1);
        }
    }

}