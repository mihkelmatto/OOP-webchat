package common.networking;

public class LoginPacket extends AbstractPacket {
    private String username, password;

    public LoginPacket(String username, String paassword) {
        this.username = username;
        this.password = paassword;
    }

    public LoginPacket() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
