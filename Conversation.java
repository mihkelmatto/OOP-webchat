import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Conversation {
    private int id;
    private ArrayList<String> participants;
    private ArrayList<HashMap<String, Object>> messages;

    /*
    create a new Conversation object
    id - unique id for this conversation.
        TODO: automatically manage id
    
    users: Strings of usernames
        Takes any number of args
        TODO: String -> User

    participants: internal users list
        members can be added/removed later
        used for creating entries to this.messages

    messages: entries should be in this format:
    {
    "sender": String username,
    "timestamp": "mm:hh-dd:mm:yyyy", // timezone?
    "content": String message
    }
    */
    public Conversation(int id, String... users){
        this.id = id;
        
        this.participants = new ArrayList<>();
        for(String user : users) this.participants.add(user);

        this.messages = new ArrayList<>();
        System.out.printf("""
                Chat created
                id: %s
                participants: %s                
                
                """, this.id, this.participants);
    }

    public void addMember(String member){
        if(!this.participants.contains(member)){
            this.participants.add(member);
            System.out.printf("Member %s added to conversation %s\n", member, this.id);
        }
        else{
            System.out.printf("Member %s already in conversation %s\n", member, this.id);
        }
    }

    // TODO: delete conversation when member count reaches 0
    public void removeMember(String member){
        if(this.participants.contains(member)){
            this.participants.remove(member);
            System.out.printf("Member %s removed from conversation %s\n", member, this.id);
        }
        else{
            System.out.printf("Member %s can't be removed from conversation %s\n", member, this.id);
        }
    }

    /*
    Adds an entry in this format:
    {
        "sender": String username,
        "timestamp": "mm:hh-dd:mm:yyyy", // timezone?
        "content": String message
    }

    */

    public void addMessage(String sender, String message){
        LocalDateTime datetime = LocalDateTime.now();

        HashMap<String, Object> entry = new HashMap<>();
        entry.put("sender", sender);
        entry.put("timestamp", datetime);
        entry.put("message", message);
        
        this.messages.add(entry);
    }

    /* 
    TODO: what's the identification for a removed message?
    is timestamp always unique?
    maybe timestamp+sender?
    */
    public void removeMessage(String message){

    }

    public void printAll(){
        for(HashMap<String, Object> entry : this.messages){
            System.out.println(entry);
        }
        System.out.println("");
    }

    @Override
    public String toString(){
        return String.format("""
                Chat ID: %s
                Participants: %s
                Message count: %s
                """,this.id, this.participants, this.messages.size());
    }
}
