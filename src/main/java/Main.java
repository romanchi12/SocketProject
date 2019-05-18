public class Main {

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
        Thread.sleep(1000);
        for(int i = 0; i < 4; i++){
            new Client().start();
        }

    }
}
