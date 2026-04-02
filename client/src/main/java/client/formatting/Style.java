package client.formatting;

// See võiks mingi bit field olla
public class Style {
    private boolean italic;
    private boolean bold;
    private boolean mention;

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setMention(boolean mention) {
        this.mention = mention;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isMention() {
        return mention;
    }

    @Override
    public String toString() {
        return "Style{" +
                "italic=" + italic +
                ", bold=" + bold +
                ", mention=" + mention +
                '}';
    }
}
