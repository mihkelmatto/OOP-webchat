package common.networking;

public class AddChannelResponsePacket extends AbstractPacket {
    private String channelName;

    public AddChannelResponsePacket(String channelName) {
        this.channelName = channelName;
    }

    public AddChannelResponsePacket() {
    }

    public String getChannelName() {
        return channelName;
    }
}
