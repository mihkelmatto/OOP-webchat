package client;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.UnknownHostException;
import java.util.function.Consumer;

public class LoginScene extends Scene {
    public LoginScene(Consumer<Scene> switchScene, double w, double h) {
        // Jõle kahtlane, aga see töötab
        super(new VBox(), w, h);

        TextField ipField = new TextField();
        TextField portField = new TextField();
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Connect");

        loginButton.setOnAction(e -> {
            // TODO: selle kontrolli võiks liigutada ClientConnection konstruktorisse
            if (ipField.getText().isEmpty() || userField.getText().isEmpty() || passField.getText().isEmpty()) {
                return;
            }

            try {
                ClientConnection conn = new ClientConnection(userField.getText(), ipField.getText(), portField.getText());
                switchScene.accept(new MessageScene(conn, w, h));
            } catch (UnknownHostException ex) {
                // TODO: log error
            }
        });

        setRoot(new VBox(
                new HBox(new Label("IP: "), ipField),
                new HBox(new Label("port: "), portField),
                new HBox(new Label("user: "), userField),
                new HBox(new Label("pass: "), passField),
                loginButton
        ));
    }
}
