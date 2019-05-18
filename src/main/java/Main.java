import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
        Thread.sleep(1000);
        List<Client> clients = new LinkedList<>();
        for(int i = 0; i < 4; i++){
            Client client = new Client();
            clients.add(client);
            client.start();
        }
        Thread.sleep(15000);
        clients.forEach(Thread::interrupt);
    }
}
