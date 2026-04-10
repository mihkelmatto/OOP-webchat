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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stseen sõnumite vaateks. Saab vahetada kanaleid (TBD), kirjutada sõnumeid
 * ja lugeda sõnumeid.
 */
public class MessageScene extends Scene {
    // Kõik sõnumid UI komponentidena.
    private final Map<String, MessageList> channels;
    private List<String> channelNames = List.of("general", "uudised");
    private String selectedChannel = channelNames.get(0);
    private ScrollPane scrollPane;
    private VBox channelList;


    public MessageScene(ClientConnection conn, double w, double h) {
        // Midagi peame parentiks panema, paneme HBox
        super(new HBox(), w, h);

        channels = new HashMap<>();
        
        // Vasakpoolne osa, kus on kanalid
        // TODO: see on ainult UI testimiseks, tuleks küsida kanalite nimekirja serverilt.
        channelList = new VBox();
        channelList.setFillWidth(true);

        // Sõnumite vaade
        scrollPane = new ScrollPane();
        for (String channelName : channelNames){
            addChannel(channelName);
        }
        scrollPane.setContent(channels.get(selectedChannel));
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Scrollib alla kui uus sõnum tuleb

        // Sõnumite kirjutamiseks
        TextField messageField = createMessageField(conn, scrollPane);

        // Ekraani parempoolne osa (sõnumid ja kast sõnumi kirjutamiseks)
        VBox messagesRoot = new VBox(scrollPane, messageField);
        HBox.setHgrow(messagesRoot, Priority.ALWAYS);




        // Lõpuks vahetame rooti välja
        // TODO: kindlasti seda saab kuidagi ilusamalt teha, see on hästi rõve.
        HBox root = new HBox(channelList, messagesRoot);
        setRoot(root);

        // Kui UI on loodud, kleebime sinna otsa ühenduse serveriga
        conn.setOnMessageReceived(this::addMessageToUI);
        Thread.ofVirtual().start(conn);
    }

    /**
     * Initsialiseerib tekstikastikese sõnumite kirjutamiseks.
     *
     * @param conn       viit ühendusele serveriga.
     * @param scrollPane viit ScrollPane'ile, mis sisaldab sõnumeid.
     * @return loodud tekstikastike koos oma event handler'iga.
     */
    private TextField createMessageField(ClientConnection conn, ScrollPane scrollPane) {
        TextField messageField = new TextField();

        // Kui vajutatakse enter, st tahetakse kirjutatut ära saata.
        messageField.setOnAction(e -> {
            // Tühi, edasi pole midagi teha.
            if (messageField.getText().isEmpty()) {
                return;
            }

            // Saadame ära.
            conn.queueMessage(messageField.getText(), selectedChannel);

            // Teeme tekstikastikese tühjaks.
            messageField.clear();

            // Skrollime alla.
            // TODO: miks on see vajalik, kui scrollPane peaks juba ise seda tegema (vt MessageScene konstruktorit).
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });

        return messageField;
    }
    /**
     * kanali lisamiseks funktsioon. Lisab nupu, 
     * @param channelName
     */
    private void addChannel(String channelName){
        if (channels.containsKey(channelName)){
            return;
        }
        MessageList messages = new MessageList();
        messages.heightProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(() -> scrollPane.setVvalue(1.0)));
        channels.put(channelName, messages);
        Button channelButton = new Button(channelName);
        channelButton.setMaxWidth(Double.MAX_VALUE);
        channelButton.setOnAction(e -> {
            selectedChannel = channelName;
            scrollPane.setContent(this.channels.get(selectedChannel));
        });;
        channelList.getChildren().add(channelButton);
    }

    /**
     * Lisab sõnumi sõnumite nimekirja. Seda meetodit võib välja kutsuda teisest
     * lõimest!
     *
     * @param payload sõnum, kus on username, channelName ja content peavad olema eraldatud "\t;" eraldajaga
     */
    private void addMessageToUI(String payload) {
        // Platform.runLater() paneb selle lambda kuskile järjekorda ja see
        // täidetakse hiljem. Otse ei tohi me teisest lõimest JavaFX olekut
        // muuta, sest see teeks kõik katki.
        Platform.runLater(() -> {
            String[] pieces = payload.split("\t;");
            if (pieces.length == 3){
                String username = pieces[0];
                String channelName = pieces[1];
                String content = pieces[2].replace("\\t;", "\t;"); // unescapime
                MessageList channel =  channels.get(channelName);
                if (channel != null){
                    channel.addMessage(username, content);
                }
            }
        });
    }
}
