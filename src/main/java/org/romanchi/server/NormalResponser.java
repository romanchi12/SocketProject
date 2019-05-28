package org.romanchi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Principal;
import java.util.logging.Logger;

public class NormalResponser implements Responser {

    private final static Logger logger = Logger.getLogger(NormalResponser.class.getCanonicalName());
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public NormalResponser(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public String read() throws IOException {
        String request, response;
        while ((request = in.readLine()) != null){
            logger.info("request: " + request);
        }
        return "werw";
    }

    @Override
    public void send(Object object) {
        logger.info("response: " + object.toString());
        out.write("response: " + object.toString());
        out.flush();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted() || !clientSocket.isConnected()){
            try {
                String data = read();
                if("exit".equals(data)){
                    break;
                }
                send(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
