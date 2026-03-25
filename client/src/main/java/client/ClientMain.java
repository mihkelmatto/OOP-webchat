package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClientMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello, world!");
        Scene scene = new Scene(label);

        stage.setScene(scene);
        stage.setTitle("Hello, world");
        stage.show();
    }
}
