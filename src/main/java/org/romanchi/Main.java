package org.romanchi;

import com.sun.media.jfxmedia.logging.Logger;
import org.romanchi.client.Client;
import org.romanchi.client.FileDescriptor;
import org.romanchi.server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        Logger.setLevel(Logger.ERROR);
        File file = new File("D:\\завантаження\\фон.png");
        long filesize = file.length();
        Server server = new Server();
        server.start();
        Thread.sleep(1000);
        Client client = new Client();

        client.setFileToDownload(
                FileDescriptor.builder()
                        .fileName("D:\\завантаження\\E3k3t")
                        .fileSize(filesize)
                        .build());

        client.start();
    }
}
