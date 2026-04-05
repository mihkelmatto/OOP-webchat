package client;

import client.ui.LoginScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
    private Stage stage = null;
    private String stylesheet = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // See värk on ilgelt kahtlane
        this.stage = stage;

        LoginScene scene = new LoginScene(this::switchScene, 640, 480);
        switchScene(scene);

        stage.show();
    }

    private String getStyleSheet() {
        if (stylesheet == null) {
            stylesheet = String.valueOf(getClass().getResource("/style.css"));
        }
        return stylesheet;
    }

    private void switchScene(Scene scene) {
        scene.getStylesheets().add(getStyleSheet());
        stage.setScene(scene);
    }
}
