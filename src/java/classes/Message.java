/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Marco
 */
public class Message {
    private final String msg;
    private final boolean error;

    public Message(String msg, boolean error) {
        this.msg = msg;
        this.error = error;
    }

    public String getMessage() {
        return msg;
    }
    
    public boolean isError() {
        return error;
    }
    
    public String getExtentedMessage(){
        String ex_msg = null;
        if(this.msg != null){
            switch(this.msg){
                /* LOGIN */
                    case "log":
                        ex_msg = "Please log in to see this page";
                        break;
                    case "usr":
                        ex_msg = "User does not exist";
                        break;
                    case "psw":
                        ex_msg = "Incorrect password";
                        break;
                /* SIGNUP */
                        
                        
                        
                /* SETTINGS */
                        
                        
                        
                default:
                    ex_msg = null;
            }
        }
        

        return ex_msg;
    }

}
