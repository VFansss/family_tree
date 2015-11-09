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
        Iterator iter = this.iterator();
        // Iteratore di supporto per eliminare gli utenti intermedi
        Iterator iter_delete;
        // Iteratore inverso
        ListIterator iterator_reverse;
        User user;
        UserList family_core;
        // Lista che contiene gli utenti già controllati
        UserList user_checked = new UserList();
        // Flag per controllare se è stato eliminato qualche utente
        boolean deleted = false;

        // Per ogni utente ({user}) nella breadcrumb (dal primo all'ultimo inserito)
        while(iter.hasNext()){
            user = ((TreeNode) iter.next()).getUser();
            // Se è stato già verficato, passo all'utente successivo
            if(user_checked.contains(user)) continue;
            try {
                // Recupera il nucleo familiare dell'utente
                family_core = user.getFamilyCore();
                // Imposta iteratore inverso
                iterator_reverse = this.listIterator(this.size());
                // Per ogni utente ({user_to_check}) della breadcrumb (dall'ulitmo al primo inserito)
                while(iterator_reverse.hasPrevious()) {
                    User user_to_check = ((TreeNode) iterator_reverse.previous()).getUser();
                    // Se {user_to_check} è uguale a {user}, esci dal ciclo
                    if(user.equals(user_to_check)) break;
                    // Se {user_to_check} fa parte del nucleo familiare di {user} 
                    if(family_core.contains(user_to_check)){
                        // Imposta iteratore di supporto
                        iter_delete = this.iterator();
                        // Porta l'iteratore di supporto all'utente puntato dall'iteratore principale
                        while(iter_delete.hasNext()){
                            if(user.equals(((TreeNode) iter_delete.next()).getUser())) break;                                        
                        }
                        // Per ogni utente da eliminare
                        while(iter_delete.hasNext()){
                            User user_to_delete = ((TreeNode) iter_delete.next()).getUser();
                            // Se non è uguale a {user_to_check}
                            if(!user_to_delete.equals(user_to_check)){
                                // Rimuovilo
                                iter_delete.remove();
                                // Segnala che è stato eliminato un utente
                                deleted = true;

                            }else{
                                // Altrimenti, esci dall'iteratore di eliminazione per non eliminare gli utenti successivi
                                break;
                            }
                        }

                    }
                    // Se è stato eliminato un utente
                    if(deleted){
                        // Reinizializza l'iteratore prinpipale
                        deleted = false;
                        iter = this.iterator();
                        break;
                    }
                }


            } catch (SQLException ex) { }
            // Aggiungi {user} nella lista degli utenti controllati
            user_checked.add(user);
        }
    }

}
