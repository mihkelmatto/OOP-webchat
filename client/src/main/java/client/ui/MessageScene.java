package client.ui;

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
    private final MessageList messages;

    public MessageScene(ClientConnection conn, double w, double h) {
        // Midagi peame parentiks panema, paneme HBox
        super(new HBox(), w, h);

        // Ekraani vasakpoolne osa, kus on kanalid
        ChannelList channelList = new ChannelList();
        channelList.addChannel("#general");
        channelList.addChannel("#ch1");
        channelList.addChannel("#ch2");

        // Sõnumite vaade
        messages = new MessageList();
        ScrollPane scrollPane = new ScrollPane(messages);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Scrollib alla kui uus sõnum tuleb
        messages.heightProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(() -> scrollPane.setVvalue(1.0)));

        // Ekraani parempoolne osa (sõnumid ja kast sõnumi kirjutamiseks)
        VBox messagesRoot = createMessagesSide(conn, channelList, scrollPane);

        // Lõpuks vahetame rooti välja
        // TODO: kindlasti seda saab kuidagi ilusamalt teha, see on hästi rõve.
        HBox root = new HBox(channelList, messagesRoot);
        setRoot(root);

        // Kui UI on loodud, kleebime sinna otsa ühenduse serveriga
        conn.setOnMessageReceived(this::addMessageToUI);
        Thread.ofVirtual().start(conn); // See võib failida, peaks tagasi login ekraanile viskama
    }

    /**
     * Loob ekraani parempoolse osa, mis sisaldab sõnumeid ka kastikest nende
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

    private void addMessageToUI(MessageToClientPacket packet) {
        Platform.runLater(() -> {
            // TODO:
            messages.addMessage("placeholder", packet.getContent());
        });
    }
}
