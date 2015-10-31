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
    private String msn;
    private boolean error;

    public Message(String msn, boolean error) {
        this.msn = msn;
        this.error = error;
    }

    public String getMessage() {
        return msn;
    }
    
    public boolean isError() {
        return error;
    }

}
