package it.univaq.ingweb.collaborative;

import java.util.LinkedHashSet;

/**
 *
 * @author Marco
 */
public final class UserList extends LinkedHashSet<User>{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2917370506271262397L;

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
