package common.networking;

import java.sql.Timestamp;

public class MessageToClientPacket extends MessageToServerPacket {
    private Timestamp timestamp = null;
    private String user;

    public MessageToClientPacket(String targetChannel, String user, String content, Timestamp timestamp) {
        super(targetChannel, content);
        this.timestamp = timestamp;
        this.user = user;
    }

    public MessageToClientPacket(MessageToServerPacket incoming, String user, Timestamp timestamp) {
        super(incoming.getTargetChannel(), incoming.getContent());
        this.timestamp = timestamp;
        this.user = user;
    }

    public MessageToClientPacket() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUser(){
        return user;
    }
}
