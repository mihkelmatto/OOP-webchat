import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> users = new ArrayList<>();
        Conversation conversation = new Conversation(0,
            "user1", "user2");
        
        conversation.addMessage("user1", "testing testing");
        conversation.addMessage("user2", "abcdefg");
        conversation.printAll();

        System.out.println(conversation);
        conversation.addMember("user3");
        conversation.addMember("user3");
        System.out.println(conversation);
        conversation.removeMember("user3");
        conversation.removeMember("user3");
    }
}
