package server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionHandler implements Runnable, Closeable {
    private final LinkedBlockingQueue<String> localMessageEvents = new LinkedBlockingQueue<>();
    private final CopyOnWriteArraySet<ConnectionHandler> allConnectionHandlers;
    private final Socket clientSocket;

    public ConnectionHandler(CopyOnWriteArraySet<ConnectionHandler> allConnectionHandlers, Socket clientSocket) {
        this.allConnectionHandlers = allConnectionHandlers;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        allConnectionHandlers.add(this);

        // TODO: kogu selles asjas on vaja tagada, et see thread viisakalt
        //  ennast ära tapab siis, kui klient ühenduse katkestab.
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            Thread receiver = Thread.ofVirtual().start(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String line = in.readLine();
                        if (line != null) {
                            broadcastMessage(line, false);
                        }
                    } catch (IOException ignored) {
                        break;
                    }
                }
            });

            queueClientMessage("Welcome to the test server!");

            while (!Thread.currentThread().isInterrupted()) {
                String message = localMessageEvents.take();
                out.println(message);
                out.flush(); // TODO: tegelt pole hea iga kord flushida, aga see tekitas varem mingeid probleeme
            }

            receiver.interrupt();
        } catch (IOException ignored) {
            // TODO: log exception, but don't crash!
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        allConnectionHandlers.remove(this);
    }

    // Varem oli see synchronized, aga nüüd vist pole tarvis?
    private void queueClientMessage(String message) {
        localMessageEvents.add(message);
    }

    private void broadcastMessage(String message, boolean ignoreSelf) {
        for (ConnectionHandler conn : allConnectionHandlers) {
            if (ignoreSelf && conn == this) {
                continue;
            }
            conn.queueClientMessage(message);
        }
    }
}
