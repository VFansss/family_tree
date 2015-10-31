/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes_asdsa;

import java.util.LinkedHashSet;

/**
 *
 * @author Marco
 */
public final class UserList extends LinkedHashSet<User>{
    
    /**
     * Verifica se un utente appartiene ad una lista
     * @param user  utente da ricercare
     * @return      true se l'utente appartiene alla lista, false altrimenti
     */
    
    @Override
    public boolean add(User user){
        if(user != null){
            return super.add(user);
        }
        return false;
    }
    
    /** Debugging: mostra tutti i nomi degli utenti appartenenti alla lista
     *
     */
    public void analize(){
        for(User element: this){
            Database.out.println(element.getName());
        }
    }
    
}
