package client;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class LoginScene extends Scene {
    public LoginScene(Consumer<Scene> switchScene, double v, double v1) {
        super(new VBox(), v, v1);

        TextField ipField = new TextField();
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Connect");

        loginButton.setOnAction(e -> {
            if (ipField.getText().isEmpty() || userField.getText().isEmpty() || passField.getText().isEmpty()) {
                return;
            }
            switchScene.accept(new MessageScene(v, v1));
        });

        setRoot(new VBox(
                new HBox(new Label("IP"), ipField),
                new HBox(new Label("username"), userField),
                new HBox(new Label("password"), passField),
                loginButton
        ));
    }
}
