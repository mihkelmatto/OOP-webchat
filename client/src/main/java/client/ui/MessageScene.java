package client.ui;

import client.ClientConnection;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class MessageScene extends Scene {
    private final MessageList messages;

    public MessageScene(ClientConnection conn, double w, double h) {
        // Midagi peame parentiks panema, paneme HBox
        super(new HBox(), w, h);

        // Sõnumite vaade
        messages = new MessageList();
        ScrollPane scrollPane = new ScrollPane(messages);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Scrollib alla kui uus sõnum tuleb
        messages.heightProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(() -> scrollPane.setVvalue(1.0)));

        // Sõnumite kirjutamiseks
        TextField messageField = new TextField();
        messageField.setOnAction(e -> {
            if (messageField.getText().isEmpty()) {
                return;
            }
            conn.sendMessage(messageField.getText());
            messageField.clear();
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });

        // Ekraani parempoolne osa
        VBox messagesRoot = new VBox();
        HBox.setHgrow(messagesRoot, Priority.ALWAYS);
        messagesRoot.getChildren().addAll(scrollPane, messageField);

        // Vasakpoolne osa, kus on kanalid
        VBox channelList = createChannelList(List.of("#general", "#ch1", "#ch2"));

        // Lõpuks vahetame rooti välja
        HBox root = new HBox(channelList, messagesRoot);
        setRoot(root);

        // Kui UI on loodud, kleebime sinna otsa ühenduse serveriga
        conn.setOnMessageReceived(this::addMessageToUI);
        Thread.ofVirtual().start(conn);
    }

    private static VBox createChannelList(List<String> channels) {
        VBox channelList = new VBox();
        channelList.setFillWidth(true);

        for (String channelName : channels) {
            Button channelButton = new Button(channelName);
            channelButton.setMaxWidth(Double.MAX_VALUE);
            channelList.getChildren().add(channelButton);
        }

        return channelList;
    }

    private synchronized void addMessageToUI(String content) {
        Platform.runLater(() -> {
            String username = "kasutaja"; // todo
            messages.addMessage(username, content);
        });
    }
}
