package client.ui;

import java.util.HashMap;
import java.util.Map;

import client.ClientConnection;
import common.networking.MessageToClientPacket;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Stseen sõnumite vaateks. Saab vahetada kanaleid (TBD), kirjutada sõnumeid
 * ja lugeda sõnumeid.
 */
public class MessageScene extends Scene {
    // Kõik sõnumid UI komponentidena.
    private Map<String, MessageList> channels = new HashMap<>();
    private ScrollPane scrollPane;
    private ChannelList channelList;

    public MessageScene(ClientConnection conn, double w, double h) {
        // Midagi peame parentiks panema, paneme HBox
        super(new HBox(), w, h);

        // Ekraani vasakpoolne osa, kus on kanalid
        channelList = new ChannelList();
        channelList.setOnChannelChange(newChannel -> {
            scrollPane.setContent(channels.get(newChannel));
        });
        
        // Sõnumite vaade
        addChannel("#general"); // vaikimisi channel
        scrollPane = new ScrollPane(channels.get("#general"));
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);


        // Ekraani parempoolne osa (sõnumid ja kast sõnumi kirjutamiseks)
        VBox messagesRoot = createMessagesSide(conn, channelList, scrollPane);

        // Lõpuks vahetame rooti välja
        // TODO: kindlasti seda saab kuidagi ilusamalt teha, see on hästi rõve.
        HBox root = new HBox(channelList, messagesRoot);
        setRoot(root);

        // Kui UI on loodud, kleebime sinna otsa ühenduse serveriga
        conn.setOnMessageReceived((msg) -> Platform.runLater(() -> {
            MessageList channel = channels.get(msg.getTargetChannel());
            if (channel != null){
                channel.addMessage(msg.getUser(), msg.getContent());
            }
        }));

        conn.setOnChannelAdded((channelPacket) -> Platform.runLater(() -> {
            addChannel(channelPacket.getChannelName());
        }));

        Thread.ofVirtual().start(conn); // See võib failida, peaks tagasi login ekraanile viskama
        conn.requestChannelList();
    }
    
    /**
     * Teeb kõik, mida on vaja, et kliendil uus kanal tekiks
     * @param channelName
     */
    private void addChannel(String channelName){
        if (channels.containsKey(channelName)) return;
        MessageList channel = new MessageList();
        // Teeb, et scrollib alla kui uus sõnum tuleb
        channel.heightProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(() -> scrollPane.setVvalue(1.0)));
        channels.put(channelName, channel);
        channelList.addChannel(channelName);
    }

    /**
     * Loob ekraani parempoolse osa, mis sisaldab sõnumeid ja kirjutus välja nende
     * kirjutamiseks.
     * @param conn ühendus serveriga
     * @param channelList kanalite ui komponent
     * @param scrollPane ScrollPane, mille sees sõnumid istuvad
     * @return ekraani parempoolne osa
     */
    private static VBox createMessagesSide(ClientConnection conn, ChannelList channelList, ScrollPane scrollPane) {
        TextField messageField = new TextField();
        messageField.setOnAction(e -> {
            if (messageField.getText().isEmpty()) {
                return;
            }
            conn.sendMessage(channelList.getActiveChannel(), messageField.getText());
            messageField.clear();
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });

        VBox messagesRoot = new VBox(scrollPane, messageField);
        HBox.setHgrow(messagesRoot, Priority.ALWAYS);
        return messagesRoot;
    }
}
