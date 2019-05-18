import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class Responser extends Thread {

    private final static Logger logger = Logger.getLogger(Responser.class.getName());

    private Socket clientSocket;

    public Responser(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        String data = "";
        while(!isInterrupted() || "exit".equals(data)){
            data = read();
        }
    }

    private String read(){
        try {
            InputStream inputStream = clientSocket.getInputStream();
            byte [] buffer = new byte[1024*1024];
            String data = "";
            int dataLength = inputStream.read(buffer);
            data = data.concat(new String(buffer, 0, dataLength));
            logger.info("SERVER READ: " + data);
            logger.info("DATA LENGTH: " + dataLength);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
