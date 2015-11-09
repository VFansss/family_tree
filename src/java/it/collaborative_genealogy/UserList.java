package it.collaborative_genealogy;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.LinkedHashSet;

/**
 *
 * @author Marco
 */
public final class UserList extends LinkedHashSet<User>{
    
    /**
     * Aggiungi un utente ad una lista
     * @param user  utente da aggiungere
     * @return      true se l'utente è stato aggiungo alla lista, false altrimenti
     */
    @Override
    public boolean add(User user){
        if(user != null){
            return super.add(user);
        }
        return false;
    }
    
    
}
