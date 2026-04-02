import java.util.ArrayList;

public class Conversation {
    private int convID;
    private User owner;
    private ArrayList<User> members;
    private ArrayList<Entry> messages;
    private int entryID;

    // customization
    private String convName;

    /*
    convID      unique id for this conversation.
    owner       has permissions to add/remove members
    messages    ArrayList<Entry>, see Entry class for more info
    entryID     internal counter & instance field value for entries. !! Dont change outside of this.addmessage()
    
    members: internal users list
        used for:
        - creating entries to this.messages
        - validation for server>user sync?



    !! Methods without an access modifier are meant to be called from User.java (TODO: option to use server-side admin cmds?)

    Permissions for regular users:
    - send, delete, edit their messages (TODO: edit messages)
    - leave the server via removemember

    Additional permissions for chat's owner:
    - add/remove any users
    - can't leave the chat unless they are the last person (TODO)
    - transfer ownership (TODO)
    */

    public Conversation(int convID, User owner){
        // init
        this.convID = convID;
        this.owner = owner;
        this.members = new ArrayList<User>();
        this.messages = new ArrayList<Entry>();
        this.entryID = 0;
        this.convName = String.format("%s's chat", owner.getUsername());

        // other
        this.members.add(owner);
        System.out.printf("Chat created with ID: %s\n", this.convID);
    }

    /*
    Validation:
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
        if(!this.owner.equals(caller) && !caller.equals(member)){
            validrequest = false;
            msg = "This can only be used by the owner";
        }
        else if(member.equals(this.owner)){
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

    void addEntry(User caller, String message){
        // validation
        if(!members.contains(caller)){
            System.out.printf("Message failed: %s is not in this conversation\n", caller.getUsername());
            return;
        }
        // add message
        this.messages.add(new Entry(this.entryID++, caller, message));
    }

    /*
    Validation:
    - has to be a member
    - has to be the author of this entry
    */
    void removeEntry(User caller, Entry entry){
        String msg = null;
        boolean validrequest = true;

        // validation
        if(!members.contains(caller)){
            validrequest = false;
            msg = String.format("%s is not in this conversation", caller.getUsername());
        }
        else if(!entry.getSender().equals(caller)){
            validrequest = false;
            msg = "message can only be deleted by the author";   
        }
        else if(!messages.contains(entry)){
            validrequest = false;
            msg = "Message not found";
        }

        // error message
        if(!validrequest){
            System.out.printf("Message deletion failed: %s\n", msg);
            return;
        }

        // deletion
        this.messages.remove(entry);
    }

    /*
    Customization
    */
    public void setConvName(String name){
        this.convName = name;
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

    @Override
    public String toString(){
        return String.format("""
                Chat name: %s
                Owner: %s
                Members: %s
                Message count: %s
                """,this.convName, this.owner.getUsername(), this.strMembers(), this.messages.size());
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
