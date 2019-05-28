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

public interface Responser extends Runnable {

    Object read() throws IOException;

    void send(Object object);
}
