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

        System.out.printf("""
                Chat created
                %s
                """, this.toString());
    }

    /*
    Addmember:
    - only usable by owner
    - no duplicates
    */
    void addMember(User caller, User member){
        if(!this.owner.equals(caller)){
            System.out.printf("Can't add to conversation %s: no permissions.\n", caller.getUsername());
            return;
        }
        if(this.members.contains(member)){
            System.out.printf("Member %s already in conversation %s\n", member.getUsername(), this.convID);
            return;
        }

        this.members.add(member);
        System.out.printf("Member %s added to conversation %s\n", member.getUsername(), this.convID);
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
        String errormsg = null;
        boolean validrequest = true;

        if(!this.owner.equals(caller)){
            
        }
        if(caller.equals(member)){
            errormsg = String.format("can't remove the owner", null);
        }


        if(this.members.contains(member)){
            this.members.remove(member);
            System.out.printf("Member %s removed from conversation %s\n", member.getUsername(), this.convID);
        }
        else{
            System.out.printf("Member %s can't be removed from conversation %s\n", member.getUsername(), this.convID);
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
