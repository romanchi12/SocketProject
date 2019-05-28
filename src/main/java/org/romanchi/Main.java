package org.romanchi;

import org.romanchi.client.Client;
import org.romanchi.client.FileDescriptor;
import org.romanchi.server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        /*Server server = new Server();
        server.start();
        Client client = new Client();
        client.start();
        */
        /*Logger.setLevel(Logger.ERROR);
        File file = new File("D:\\завантаження\\фон.png");
        long filesize = file.length();
        */
        Server server = new Server();
        server.start();
        Thread.sleep(1000);
        Client client = new Client();
        client.start();
    }
}
