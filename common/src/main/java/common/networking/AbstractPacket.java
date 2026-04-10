package common.networking;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddChannelResponsePacket.class, name = "add_channel_response"),
        @JsonSubTypes.Type(value = GetChannelsRequestPacket.class, name = "get_channels_request"),
        @JsonSubTypes.Type(value = MessageToClientPacket.class, name = "message_to_client"),
        @JsonSubTypes.Type(value = MessageToServerPacket.class, name = "message_to_server"),
        @JsonSubTypes.Type(value = LoginPacket.class, name = "login_to_server"),
})
public abstract class AbstractPacket {

}
