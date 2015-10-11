/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class.Tree;

import Class.Database;
import Class.User;
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
        this.stream().map((element) -> {
            Database.out.print(element.getUser().getName() + ": ");
            return element;
        }).forEach((element) -> {
            Database.out.println(element.getLabel() + " <br>");
        });
    }

}
