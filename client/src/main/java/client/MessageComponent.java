package client;

import client.formatting.FormattingParser;
import client.formatting.TextSection;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

public class MessageComponent extends TextFlow {
    public MessageComponent(String author, String rawContent) {
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("pirukas-bold");
        authorLabel.setTextFill(pickUserColor(author));

        Label separator = new Label(": ");
        getChildren().addAll(authorLabel, separator);

        FormattingParser formattingParser = new FormattingParser(rawContent);
        while (formattingParser.hasNext()) {
            TextSection section = formattingParser.next();
            getChildren().add(section.asLabel());
        }

    }

    private static Color pickUserColor(String username) {
        String[] colors = {"#e6194b", "#3cb44b", "#ffe119", "#4363d8", "#f58231", "#911eb4", "#42d4f4", "#f032e6", "#bfef45", "#fabed4", "#469990", "#dcbeff", "#9a6324", "#fffac8", "#800000", "#aaffc3"};
        return Color.valueOf(colors[Math.abs(username.hashCode()) % colors.length]);
    }
}