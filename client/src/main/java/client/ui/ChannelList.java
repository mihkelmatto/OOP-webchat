package client.ui;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ChannelList extends VBox {
    private String activeChannel = null;
    private Consumer<String> changeChannel;

    public ChannelList() {
        setFillWidth(true);
    }

    public void addChannel(String name) {
        Button btn = new Button(name);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseReleased((e) -> {
            activeChannel = name;
            changeChannel.accept(name);
        });
        getChildren().add(btn);

        if (activeChannel == null) {
            activeChannel = name;
        }
    }

    public String getActiveChannel() {
        return activeChannel;
    }

    public void setOnChannelChange(Consumer<String> changeChannel){
        this.changeChannel = changeChannel;
    }
}
