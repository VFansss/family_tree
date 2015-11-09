/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.tree;

import it.collaborative_genealogy.Database;
import it.collaborative_genealogy.User;
import it.collaborative_genealogy.UserList;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author Marco
 */
public class NodeList extends ArrayList<TreeNode>{
    
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
        ListIterator iter = this.listIterator(0);
        // Iteratore inverso
        ListIterator iterator_reverse;
        // Oggetti User di supporto
        User user, user_to_check, user_to_delete;
        UserList family_core;
        // Flag per controllare se è stato eliminato qualche utente
        boolean deleted = false;
        // Per ogni utente ({user}) nella breadcrumb (dal primo all'ultimo inserito)
        while(iter.hasNext()){
            user = ((TreeNode) iter.next()).getUser();
            // Se {user} è stato già verficato, passa all'utente successivo
            try {
                // Recupera il nucleo familiare di {user}
                family_core = user.getFamilyCore();
                // Imposta iteratore inverso
                iterator_reverse = this.listIterator(this.size());
                // Per ogni utente ({user_to_check}) della breadcrumb (dall'ulitmo al primo inserito)
                while(iterator_reverse.hasPrevious()) {
                    user_to_check = ((TreeNode) iterator_reverse.previous()).getUser();
                    // Se {user_to_check} è uguale a {user}, esci dal ciclo perchè sono stati verificati tutti gli utenti potenzialmente rimuovibili
                    if(user.equals(user_to_check)) break;
                    // Se {user_to_check} fa parte del nucleo familiare di {user} 
                    if(family_core.contains(user_to_check)){
                        // Per ogni utente da eliminare
                        while(iter.hasNext()){
                            user_to_delete = ((TreeNode) iter.next()).getUser();
                            // Se non è uguale a {user_to_check}
                            if(!user_to_delete.equals(user_to_check)){
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
