package server;

import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private final List<ServerConnection.RawMessage> allMessages = new ArrayList<>();

    public void run() {
        Thread.Builder threadBuilder = Thread.ofVirtual();


        ServerConnection conn = new ServerConnection(this::addMessage);
        Thread t = threadBuilder.start(conn);


        try {
            Thread.sleep(10000);
            conn.sendClientMessage("Test message from server!");
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Connection over!");
            System.out.println(allMessages);
        }
    }

    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        server.run();
    }

    public synchronized void addMessage(ServerConnection.RawMessage message) {
        this.allMessages.add(message);
    }
}
