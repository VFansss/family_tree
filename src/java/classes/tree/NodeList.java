/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes.tree;

import classes.Database;
import classes.User;
import java.util.ArrayList;

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

    /** Debugging: mostra tutti i nomi degli membri dell'albero genalogico con relativa etichetta
     */
    public void analize(){
        for(TreeNode element: this){
            Database.out.println(element.getUser().getName() + ": " + element.getLabel());
        }
    }

}
