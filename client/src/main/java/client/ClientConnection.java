package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ClientConnection implements Runnable {
    private final String username; // TODO: Midagi sellega peale hakata? Ei tea.
    private final InetAddress ip;
    private final int port;

    private final LinkedBlockingQueue<String> queuedMessages = new LinkedBlockingQueue<>();
    private Consumer<String> onMessageReceived = null;

    public ClientConnection(String username, String ip, String port) throws UnknownHostException {
        this.username = username;
        this.ip = InetAddress.getByName(ip);
        this.port = Integer.parseInt(port);
    }

    @Override
    public void run() {
        try (Socket sock = new Socket(ip, port)) {
            // TODO: siin on hästi sarnane kood serveri ConnectionHandler
            //  klassile, äkki saaks mingi ilusama abstraktsiooni teha?
            try (PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {

                // TODO: See ei tööta. Miks?
                Thread receiver = Thread.ofVirtual().start(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            String line = in.readLine();
                            // TODO: kontrollida, et onMessageReceived ei ole null
                            if (line != null) {
                                onMessageReceived.accept(line);
                            }
                        } catch (IOException ignored) {
                            // TODO: log exception, but don't crash!
                        }
                    }
                });

                while (!Thread.currentThread().isInterrupted()) {
                    String messageToBeSent = queuedMessages.take();
                    out.println(messageToBeSent);
                    out.flush();
                }

                receiver.interrupt();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOnMessageReceived(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void sendMessage(String message) {
        queuedMessages.add(message);
    }
}
