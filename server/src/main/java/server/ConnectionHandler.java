package server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.networking.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Haldab ühe kliendi ühendust serveriga.
 */
public class ConnectionHandler implements Runnable {
    private static final Logger log = LogManager.getLogger(ConnectionHandler.class);

    // Packetite järjekord.
    private final LinkedBlockingQueue<AbstractPacket> queuedPackets = new LinkedBlockingQueue<>();

    // Viit serveri olekule
    private final ServerMain serverConnection;

    // Socket, mille kaudu suhtlus kliendiga käib.
    private final Socket clientSocket;

    // Ühendatud kasutaja kasutajanimi
    private String username;

    public ConnectionHandler(ServerMain serverConnection, Socket clientSocket) {
        this.serverConnection = serverConnection;
        this.clientSocket = clientSocket;
    }

    /**
     * Käivitab ühenduse kliendiga.
     */
    @Override
    public void run() {
        // Kui ühendus on loodud, siis server jääb ootama kliendilt
        // LoginPacketit ning alles pärast edukat autentimist tehakse see klient
        // teistele nähtavaks (registreeritakse).

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

            // Sõnumeid saadetakse selles lõimes.
            while (!Thread.currentThread().isInterrupted() && receiver.isAlive()) {
                AbstractPacket packetToBeSent = queuedPackets.take();
                String asString = objectMapper.writeValueAsString(packetToBeSent);
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
            serverConnection.unregister(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    /**
     * Lisab sõnumi selle ühenduse sõnumite järjekorda.
     *
     * @param message sõnum
     */
    public void queueClientMessage(MessageToClientPacket message) {
        queuedPackets.add(message);
    }

    private boolean isAuthenticated() {
        return username != null;
    }

    public void handlePacket(AbstractPacket packet) {
        // Kui pole veel autentinud, siis me teisi asju ei parsi
        if (!isAuthenticated() && !(packet instanceof LoginPacket)) {
            return;
        }

        switch (packet) {
            case MessageToServerPacket msg -> serverConnection.broadcastMessage(msg, username);
            case GetChannelsRequestPacket ignored -> {
                for (String channel : serverConnection.getChannelList()) {
                    queuedPackets.add(new AddChannelResponsePacket(channel));
                }
            }
            case LoginPacket login -> {
                // TODO: peame ka reaalselt salasõna kontrollima. kuigi ilmselt
                //  peaks saatma salasõna räsi, mitte lihtsalt plaintextina.
                username = login.getUsername();

                // Registreerime oma ühenduse.
                serverConnection.register(this);
            }
            default -> {
                log.warn("Unexpected packet: {}", packet);
            }
        }
    }
}
