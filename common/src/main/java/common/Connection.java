package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Connection implements Runnable {
    private final Map<String, List<Consumer<byte[]>>> subscribers;
    private final Socket sock;
    private boolean running;

    public Connection(Socket sock) {
        this.sock = sock;
        this.subscribers = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        // Use DataInputStream for structured binary/text data
        try (DataInputStream dis = new DataInputStream(sock.getInputStream())) {
            running = true;
            while (running) {
                int endpointLength = dis.readInt();
                byte[] endpointBytes = new byte[endpointLength];
                dis.readFully(endpointBytes);
                String endpoint = new String(endpointBytes, java.nio.charset.StandardCharsets.UTF_8);
                int payloadLength = dis.readInt();
                byte[] payload = new byte[payloadLength];
                dis.readFully(payload);
                if (subscribers.containsKey(endpoint)) {
                    var funcs = subscribers.get(endpoint);
                    for (var func : funcs) {
                        func.accept(payload);
                    }
                }
            }
        } catch (EOFException ex) {
            System.out.println("Client disconnected normally.");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            running = false;
        }
    }

    public boolean send(String endpoint, byte[] payload) {
        try (OutputStream os = sock.getOutputStream()) {
            DataOutputStream outputStream = new DataOutputStream(os);
            outputStream.writeInt(endpoint.getBytes().length);
            outputStream.write(endpoint.getBytes());
            outputStream.writeInt(payload.length);
            outputStream.write(payload);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }


    public void createEndpoint(String endpoint) {
        if (!subscribers.containsKey(endpoint)) {
            subscribers.put(endpoint, new ArrayList<>());
        }
    }

    public void subscribe(String endpoint, Consumer<byte[]> func) {
        createEndpoint(endpoint);
        subscribers.get(endpoint).add(func);
    }
}