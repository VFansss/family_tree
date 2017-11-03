package it.univaq.ingweb.collaborative.tree;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ListIterator;

import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.UserList;

/**
 *
 * @author Marco
 */

public class NodeList extends LinkedList<TreeNode>{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4510776253467963885L;

	/** Verifica se in una lista di nodi è presente un utente
     *
     * @param user
     * @return 
     */ 
    public boolean contains(User user){
        for(TreeNode element: this){
            if(element.getUser().equals(user)) return true;
        }
        return false;
    }
    
    public void cleaner(){
        // Iteratore principale
        ListIterator<TreeNode> iter = this.listIterator(0);
        // Iteratore inverso
        ListIterator<TreeNode> iteratorReverse;
        // Oggetti User di supporto
        User user, userToCheck, userToDelete;
        UserList familyCore;
        // Flag per controllare se è stato eliminato qualche utente
        boolean deleted = false;
        // Per ogni utente ({user}) nella breadcrumb (dal primo all'ultimo inserito)
        while(iter.hasNext()){
            user = ((TreeNode) iter.next()).getUser();
            // Se {user} è stato già verficato, passa all'utente successivo
            try {
                // Recupera il nucleo familiare di {user}
                familyCore = user.getFamilyCore();
                // Imposta iteratore inverso
                iteratorReverse = this.listIterator(this.size());
                // Per ogni utente ({userToCheck}) della breadcrumb (dall'ulitmo al primo inserito)
                while(iteratorReverse.hasPrevious()) {
                    userToCheck = ((TreeNode) iteratorReverse.previous()).getUser();
                    // Se {userToCheck} è uguale a {user}, esci dal ciclo perchè sono stati verificati tutti gli utenti potenzialmente rimuovibili
                    if(user.equals(userToCheck)) break;
                    // Se {userToCheck} fa parte del nucleo familiare di {user} 
                    if(familyCore.contains(userToCheck)){
                        // Per ogni utente da eliminare
                        while(iter.hasNext()){
                            userToDelete = ((TreeNode) iter.next()).getUser();
                            // Se non è uguale a {userToCheck}
                            if(!userToDelete.equals(userToCheck)){
                                // Rimuovilo
                                iter.remove();
                                // Segnala che è stato eliminato un utente
                                deleted = true;

                            }else{
                                 // Altrimenti, torna all'utente precedente ed esci dall'iteratore di eliminazione per non eliminare gli utenti successivi
                                iter.previous();
                                break;
                            }
                        }
                        // Se è stato eliminato un utente
                        if(deleted){
                            deleted = false;
                            // Passa a controllare l'utente successivo
                            break;
                        }
                    }
                    
                }

            } catch (SQLException ex) { }
        }
    }

}
