public class Main {
    public static void main(String[] args) {
        User[] users = {new User("user1"), new User("user2")};

        Conversation conversation = new Conversation(0, users[0]);
        conversation.addMember(users[0], users[1]);
        

        for(int i = 0; i<users.length; i++){
            conversation.addMessage(users[i], String.format("Message %s content", i));
        }

        conversation.printAll();

        System.out.println(conversation);
    }
}
