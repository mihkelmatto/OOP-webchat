package server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.networking.AbstractPacket;
import common.networking.GetChannelsRequestPacket;
import common.networking.MessageToClientPacket;
import common.networking.MessageToServerPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Haldab ühe kliendi ühendust serveriga.
 */
public class ConnectionHandler implements Runnable {
    private static final Logger log = LogManager.getLogger(ConnectionHandler.class);

    // Packetite järjekord.
    private final LinkedBlockingQueue<AbstractPacket> queuedPackets = new LinkedBlockingQueue<>();

    // Viit kõikide aktiivsete ühenduste hulgale (vajame seda registreerimiseks).
    private final CopyOnWriteArraySet<ConnectionHandler> allConnectionHandlers;

    // Socket, mille kaudu suhtlus kliendiga käib.
    private final Socket clientSocket;

    public ConnectionHandler(CopyOnWriteArraySet<ConnectionHandler> allConnectionHandlers, Socket clientSocket) {
        this.allConnectionHandlers = allConnectionHandlers;
        this.clientSocket = clientSocket;
    }

    /**
     * Käivitab ühenduse kliendiga.
     */
    @Override
    public void run() {
        // Registreerime oma ühenduse.
        register();

        // TODO: kogu selles asjas on vaja tagada, et see thread viisakalt
        //  ennast ära tapab siis, kui klient ühenduse katkestab.
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            ObjectMapper objectMapper = new ObjectMapper();

            Thread receiver = Thread.ofVirtual().start(() -> {
                try {
                    // TODO: only instantiate factory once (in common?)
                    Reader reader = new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8);
                    JsonParser jsonParser = objectMapper.getFactory().createParser(reader);

                    while (jsonParser.nextToken() != null && !Thread.currentThread().isInterrupted()) {
                        if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                            AbstractPacket packet = objectMapper.readValue(jsonParser, AbstractPacket.class);
                            handlePacket(packet);
                        }
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            });

            // Sõnumeid *saadetakse* selles lõimes.
            while (!Thread.currentThread().isInterrupted() && receiver.isAlive()) {
                AbstractPacket packetToBeSent = queuedPackets.take();
                String asString = objectMapper.writeValueAsString(packetToBeSent);
                System.out.println("asString = " + asString);
                out.write(asString);
                out.flush();
            }

            receiver.interrupt();
            receiver.join();
        } catch (IOException e) {
            log.error(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            unregister();
        }
    }

    /**
     * Lisab selle ühenduse aktiivsete ühenduste hulka.
     */
    private void register() {
        allConnectionHandlers.add(this);
    }

    /**
     * Eemaldab selle ühenduse aktiivsete ühenduste hulgast.
     */
    private void unregister() {
        allConnectionHandlers.remove(this);
    }

    /**
     * Lisab sõnumi selle ühenduse sõnumite järjekorda.
     *
     * @param message sõnum
     */
    private void queueClientMessage(MessageToClientPacket message) {
        queuedPackets.add(message);
    }

    /**
     * Edastab sõnumi kõigile ühendatud kasutajatele.
     *
     * @param message sõnum
     */
    private void broadcastMessage(MessageToServerPacket message) {
        Timestamp now = Timestamp.from(Instant.now());
        MessageToClientPacket packetToBeSent = new MessageToClientPacket(message, now);
        for (ConnectionHandler conn : allConnectionHandlers) {
            conn.queueClientMessage(packetToBeSent);
        }
    }

    public void handlePacket(AbstractPacket packet) {
        switch (packet) {
            case MessageToServerPacket msg -> broadcastMessage(msg);
            case GetChannelsRequestPacket getChannelsRequest -> {
                // TODO: send list of channels as individual AddChannelResponsePacket packets
            }
            default -> {
                // TODO: Report unexpected packet
            }
        }
    }
}
