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

    }



    private void startClient() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(("C:\\Users\\Roman\\IdeaProjects\\ыщслуе\\src\\main\\resources\\app.properties")));
        String hostName = properties.getProperty("server.host");
        int portNumber = Integer.valueOf(properties.getProperty("server.port"));
        String threadName = Thread.currentThread().getName();
        String[] messages = new String[] { threadName + " > msg1", threadName + " > msg2", threadName + " > msg3", threadName +
                " > Done" };
        String fileName = properties.getProperty("client.file.saveto");
        int bufferSize = Integer.valueOf(properties.getProperty("client.buffer.size"));
        File file = new File(fileName);
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        try {
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            for (String ignored : messages) {
                Scanner stdIn = new Scanner(System.in);
                String userInput;
                while ((userInput = stdIn.next()) != null) {
                    out.println(userInput);
                    if(userInput.equals("get")){
                        byte[] buffer = new byte[bufferSize];
                        int readed;
                        int fileLength = 0;
                        int acumulated = 0;
                        while((readed = echoSocket.getInputStream().read(buffer)) > 0){
                            if("password".contains(new String(buffer, 0, readed))){
                                System.out.println("ending");
                                dataOutputStream.flush();
                                dataOutputStream.close();
                                break;
                            }
                            if(new String(buffer, 0, readed).startsWith("length")){
                                fileLength = Integer.valueOf(
                                        new String(buffer, 0, readed).split(" ")[1]);
                                System.out.println("file length " + fileLength);
                                continue;
                            }
                            dataOutputStream.write(buffer, 0, readed);
                            dataOutputStream.flush();
                            acumulated += readed;
                            double percent = ((float) acumulated / fileLength) * 100;
                            System.out.println("[client] Downloading .... " +  (percent > 100 ? 100: percent)+ "%");
                        }
                    }
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