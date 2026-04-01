import java.util.ArrayList;

public class Conversation {
    private int convID;
    private User owner;
    private ArrayList<User> members;
    private ArrayList<Entry> messages;

    private int entryID;

    /*
    convID - unique id for this conversation.
        TODO: automatically manage id

    owner - has permissions to add/remove members
    
    members: internal users list
        can be managed by owner
        used for:
        - creating entries to this.messages
        - TODO: validation for server>user sync 

    messages: ArrayList<Entry>, see Entry class for more info
    entryID: internal counter & instance field value for entries. !! Dont change outside of this.addmessage()


    TODO: Check access modifiers for all classes in the branch
    */

    public Conversation(int convID, User owner){
        this.convID = convID;
        this.owner = owner;
        this.members = new ArrayList<User>();
        this.messages = new ArrayList<Entry>();
        this.entryID = 0;

        this.members.add(owner);

        System.out.printf("Chat created with ID: %s\n", this.convID);
    }

    /*
    Addmember:
    - only usable by owner
    - no duplicates
    */
    void addMember(User caller, User member){
        String msg = null;
        boolean validrequest = true;

        // validation
        if(!this.owner.equals(caller)){
            validrequest = false;
            msg = "This can only be done by the owner";
        }
        else if(this.members.contains(member)){
            validrequest = false;
            msg = String.format("%s is already a member", member.getUsername());
        }

        // error message
        if(!validrequest){
            System.out.printf("Member couldn't be added to chat %s: %s\n", this.convID, msg);
            return;
        }

        // add user
        this.members.add(member);
        System.out.printf("Member %s added to chat %s\n", member.getUsername(), this.convID);
    }

    /*
    Removes a member from the conversation

    Validation:
    - only owners can remove other Users
    - users can use this to leave
    - owners can't leave,
    - member has to exist
    - can't remove owner
    */

    void removeMember(User caller, User member){
        String msg = null;
        boolean validrequest = true;

        // validation
        if(!this.owner.equals(caller)){
            validrequest = false;
            msg = "This can only be used by the owner";
        }
        else if(caller.equals(this.owner)){
            validrequest = false;
            msg = "Owner can't be removed.";
        }
        else if(!members.contains(member)){
            validrequest = false;
            msg = String.format("Member '%s' not found", member.getUsername());
        }

        // error message
        if(!validrequest){
            System.out.printf("User %s can't be removed from chat %s : %s\n", member.getUsername(), this.convID, msg);
            return;
        }

        // removal
        this.members.remove(member);

        if(caller.equals(member)){
            System.out.printf("User %s left from chat %s\n", member.getUsername(), this.convID);
        }
        else{
            System.out.printf("User %s removed from chat %s\n", member.getUsername(), this.convID);
        }
    }

    /*
    
    Manage messages

    */

    void addMessage(User sender, String message){
        this.messages.add(new Entry(this.entryID++, sender, message));
    }

    void removeMessage(Entry message){
        this.messages.remove(message);
    }

    /*
    Get chat info (String, print)
    */

    public void printAll(){
        System.out.printf("\nAll messages from chat %s: \n\n", this.convID);
        for(Entry entry : this.messages){
            System.out.println(entry);
        }
        System.out.println("");
    }

    // Owner -> username
    @Override
    public String toString(){
        return String.format("""
                Chat ID: %s
                Owner: %s
                Members: %s
                Message count: %s
                """,this.convID, this.owner.getUsername(), this.strMembers(), this.messages.size());
    }

    private String strMembers(){
        StringBuilder temp = new StringBuilder();
        for(User member : members){
            temp.append(member.getUsername() + ", ");
        }
        temp.delete(Math.max(0, temp.length()-2), temp.length());

        return temp.toString();
    }
}
