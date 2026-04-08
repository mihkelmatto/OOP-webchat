package client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.networking.AbstractPacket;
import common.networking.AddChannelResponsePacket;
import common.networking.MessageToClientPacket;
import common.networking.MessageToServerPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Haldab kliendi ühendust serveriga.
 */
public class ClientConnection implements Runnable {
    private static final Logger log = LogManager.getLogger(ClientConnection.class);

    // Serveri detailid.
    private final InetAddress ip;
    private final int port;

    private final LinkedBlockingQueue<AbstractPacket> queuedPackets = new LinkedBlockingQueue<>();
    private Consumer<MessageToClientPacket> onMessageReceived = null;

    public ClientConnection(String ip, String port) throws UnknownHostException {
        this.ip = InetAddress.getByName(ip);
        this.port = Integer.parseInt(port);
    }

    /**
     * Käivitab ühenduse serveriga.
     */
    // TODO: sisemise handleri võiks välja abstraheerida, vt ka sarnast
    //  handler'it serveri ConnectionHandler klassis.
    @Override
    public void run() {
        try (Socket sock = new Socket(ip, port)) {
            // TODO: siin on hästi sarnane kood serveri ConnectionHandler
            //  klassile, äkki saaks mingi ilusama abstraktsiooni teha?
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
                ObjectMapper objectMapper = new ObjectMapper();

                // Sõnumite kuulamine eraldi lõimes.
                Thread receiver = Thread.ofVirtual().start(() -> {
                    try {
                        Reader reader = new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8);
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

                // Sõnumite saatmine siin lõimes.
                while (!Thread.currentThread().isInterrupted()) {
                    AbstractPacket packetToBeSent = queuedPackets.take();
                    // objectMapper.writeValue(out, packetToBeSent);
                    String packet = objectMapper.writeValueAsString(packetToBeSent);
                    System.out.println("packet = " + packet);
                    out.write(packet);
                    out.flush();
                }

                receiver.interrupt();
                receiver.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Määrab tegevuse, mis on tarvis teha saabunud sõnumi korral.
     * @param onMessageReceived event handler
     */
    public void setOnMessageReceived(Consumer<MessageToClientPacket> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    /**
     * Lisab sõnumi järjekorda, et see serverile saata.
     * @param message sõnum.
     */
    public void sendMessage(String targetChannel, String message) {
        // TODO: check for null
        queuedPackets.add(new MessageToServerPacket(targetChannel, message));
    }

    public void handlePacket(AbstractPacket packet) {
        switch (packet) {
            // TODO: check that onMessageReceived is not null
            case MessageToClientPacket msg -> onMessageReceived.accept(msg);
            case AddChannelResponsePacket addChannelResponse -> {
                // TODO: update UI and local state to add channel
            }
            default -> {
                // TODO: report unexpected packet
            }
        }
    }
}
