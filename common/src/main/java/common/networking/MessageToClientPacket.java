package common.networking;

import java.sql.Timestamp;

public class MessageToClientPacket extends MessageToServerPacket {
    private Timestamp timestamp = null;

    public MessageToClientPacket(String targetChannel, String content, Timestamp timestamp) {
        super(targetChannel, content);
        this.timestamp = timestamp;
    }

    public MessageToClientPacket(MessageToServerPacket incoming, Timestamp timestamp) {
        super(incoming.getTargetChannel(), incoming.getContent());
        this.timestamp = timestamp;
    }

    public MessageToClientPacket() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
