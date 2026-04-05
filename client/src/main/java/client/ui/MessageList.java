package client.ui;

import javafx.scene.layout.VBox;

// See peaks dünaamiliselt fetchima kuskilt andmebaasist
// Aga ma ei viitsi praegu
public class MessageList extends VBox {
    public void addMessage(String author, String content) {
        getChildren().add(new MessageComponent(author, content));
    }
}
