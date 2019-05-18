package org.romanchi;

import org.romanchi.client.Client;
import org.romanchi.client.FileDescriptor;
import org.romanchi.server.Server;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        File file = new File("D:\\завантаження\\video_dubai.mp4");
        long filesize = file.getTotalSpace();
        Server server = new Server();
        server.start();
        Thread.sleep(1000);
        Client client = new Client();

        client.setFileToDownload(
                FileDescriptor.builder()
                        .fileName("D:\\завантаження\\video_dubai.mp4")
                        .fileSize(filesize)
                        .build());

        client.start();
    }
}
