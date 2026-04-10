package server;

import common.networking.MessageToClientPacket;
import common.networking.MessageToServerPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Serveri põhiklass. TODO: ilmselt võiks maini siia tõsta hoopis.
 */
public class ServerMain {
    // Kõigi aktiivsete ühenduste hulk.
    private final CopyOnWriteArraySet<ConnectionHandler> allConnectionHandlers = new CopyOnWriteArraySet<>();

    private final List<String> channelList = Collections.synchronizedList(new ArrayList<>());

    public ServerMain() {
        // Suvalised näidiskanalid
        channelList.add("#general");
        channelList.add("#server-loodud-kanal-1");
        channelList.add("#server-loodud-kanal-2");
    }

    public static void main(String[] args) {
        new ServerMain().start();
    }

    /**
     * Käivitab serveri.
     */
    public void start() {
        // Kasutame virtuaalseid lõimesid, et ei peaks mingi async asjadega eraldi jamama.
        // TODO: teha port konfigureeritavaks.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket serverSocket = new ServerSocket(6969)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept(); // Blokib, kuni uus klient ühendab
                    executor.submit(new ConnectionHandler(this, client)); // Loob sellele vastava handler'i.
                } catch (IOException ignored) {
                    // TODO: log exception, but don't crash!
                }
            }
        } catch (IOException e) {
            // Siin võiks midagi targemat teha
            throw new RuntimeException(e);
        }
    }

    public void register(ConnectionHandler handler) {
        allConnectionHandlers.add(handler);
    }

    public void unregister(ConnectionHandler handler) {
        allConnectionHandlers.remove(handler);
    }

    public CopyOnWriteArraySet<ConnectionHandler> getAllConnectionHandlers() {
        return allConnectionHandlers;
    }

    public List<String> getChannelList() {
        return channelList;
    }

    /**
     * Edastab sõnumi kõigile ühendatud ja autenditud kasutajatele.
     *
     * @param message sõnum
     */
    public void broadcastMessage(MessageToServerPacket message, String author) {
        Timestamp now = Timestamp.from(Instant.now());
        MessageToClientPacket packetToBeSent = new MessageToClientPacket(message, author, now);
        for (ConnectionHandler conn : allConnectionHandlers) {
            conn.queueClientMessage(packetToBeSent);
        }
    }
}
