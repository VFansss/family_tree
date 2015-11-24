package it.collaborative_genealogy;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Gianluca
 */
public final class Request {
    private final User sender;
    private final User receiver;
    private final String relationship;
    
    public Request(User sender, User receiver, String relationship){
        this.sender = sender;
        this.receiver = receiver;
        this.relationship = relationship;
    }
    
    public Request(String sender_id, String receiver_id, String relationship){
        this.sender = User.getUserById(sender_id);
        this.receiver = User.getUserById(receiver_id);
        this.relationship = relationship;
    }
    
    public Request(ResultSet request) throws SQLException{
        this.sender = User.getUserById(request.getString("user_id"));
        this.receiver = User.getUserById(request.getString("relative_id"));
        this.relationship = request.getString("relationship");
    }
    
    public User getSender(){
        return this.sender;
    }
    
    public User getReceiver(){
        return this.receiver;
    }
    
    public String getRelationship(){
        return this.relationship;
    }
}
