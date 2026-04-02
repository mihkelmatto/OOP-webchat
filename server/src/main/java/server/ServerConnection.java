package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ServerConnection implements Runnable {
    private volatile boolean stillRunning = true;
    private Consumer<RawMessage> consumer;
    private Queue<String> messageQueue = new LinkedBlockingQueue<>();

    public ServerConnection(Consumer<RawMessage> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(6969)) {
            Socket client = serverSocket.accept();
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
            ) {
                while (stillRunning) {
                    // Send to client
                    String newMessage;
                    while ((newMessage = messageQueue.poll()) != null) {
                        out.write("incoming message: " + newMessage + "\n");
                        out.flush();
                    }

                    // Read from client
                    String messageFromClient = in.readLine();
                    if (!messageFromClient.equals("exit")) {
                        consumer.accept(new RawMessage(messageFromClient, client.getLocalAddress().hashCode()));
                        sendClientMessage("(echo)" + messageFromClient);
                    } else {
                        endConnection();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void endConnection() {
        this.stillRunning = false;
    }

    public synchronized void sendClientMessage(String message) {
        messageQueue.add(message);
    }

    public record RawMessage(String content, int ip) {
    }
}
