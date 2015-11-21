/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.exception;

import it.collaborative_genealogy.util.Message;

/**
 *
 * @author Marco
 */
public class NotAllowedException extends Throwable {
    
    
    public NotAllowedException(String msg){
        
        super(new Message(msg, true).getMsg());
    }
    
}
