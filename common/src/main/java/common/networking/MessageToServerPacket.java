package common.networking;

public class MessageToServerPacket extends AbstractPacket {
    private String targetChannel = null;
    private String content = null;

    public MessageToServerPacket(String targetChannel, String content) {
        this.targetChannel = targetChannel;
        this.content = content;
    }

    public MessageToServerPacket() {}

    public String getTargetChannel() {
        return targetChannel;
    }

    public String getContent() {
        return content;
    }
}
