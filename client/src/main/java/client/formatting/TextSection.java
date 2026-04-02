package client.formatting;

import javafx.scene.control.Label;

public class TextSection {
    private final CharSequence content;
    private final Style style;

    public TextSection(CharSequence content, Style style) {
        this.content = content;
        this.style = style;
    }

    /**
     * Transforms the text section into a sequence of ANSI terminal codes,
     * suitable for printing in the terminal.
     * @return styled text
     */
    public String asANSIFormatted() {
        StringBuilder sb = new StringBuilder();

        if (style.isBold()) sb.append("\u001b[1m");
        if (style.isItalic()) sb.append("\u001b[3m");
        if (style.isMention()) sb.append("\u001b[37;46m");

        sb.append(content);

        if (style.isMention()) sb.append("\u001b[39;49m");
        if (style.isItalic()) sb.append("\u001b[23m");
        if (style.isBold()) sb.append("\u001b[22m");

        return sb.toString();
    }

    public Label asLabel() {
        Label label = new Label(content.toString());

        if (style.isBold()) label.getStyleClass().add("pirukas-bold");
        if (style.isItalic()) label.getStyleClass().add("pirukas-italic");
        if (style.isMention()) label.getStyleClass().add("pirukas-mention");

        return label;
    }

    @Override
    public String toString() {
        return "TextSection{" +
                "content=" + content +
                ", style=" + style +
                '}';
    }
}
