/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy;

/**
 *
 * @author Gianluca
 */
public class Request {
    private User sender;
    private User receiver;
    private String relationship;
    
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
