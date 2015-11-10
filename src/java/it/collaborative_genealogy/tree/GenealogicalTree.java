/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.tree;

import it.collaborative_genealogy.User;
import it.collaborative_genealogy.UserList;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author Marco
 */
public class GenealogicalTree{
    // Contiene l'albero genealogico
    private final NodeList family_tree;

    public NodeList getFamily_tree() {
        return family_tree;
    }
    
    public GenealogicalTree(User user){
        this.family_tree = new NodeList();
        this.family_tree.add(new TreeNode(user, "You")); 
    }
    
    /**
     * Restiruisci l'intero albero genealogico con utenti etichettati
     * @return
     */
    public NodeList getFamilyTree() throws SQLException {
        
        // Crea albero genealogico
        this.createTree(0);
        
        /* Successivamene non ci sarà bisogno di creare tutto l'albero già dall'inizo,
            ma si creerà inizialmente solo il nucleo familiare dell'utente, 
            che poi verrà ampliato in base al parente scelto
        */
        
        return this.family_tree;
    }
    
    private void createTree(int index) throws SQLException {
        
        /** 
        *   ORDINE DI INSERIMENTO:
        *   - Genitori
        *   - Figli
        *   - Fratelli/sorelle
        *   - Coniuge
        
            N.B.: l'ordine con cui vengono inseriti i parenti è importante
            Esempio: dopo aver aggiunto i propri figli, 
                    i figli che aggiungerà il coniuge saranno sicuamente figliastri,
                    per cui è importante aggiungere prima i figli di un'utente e poi il coniuge
        */

        // Se non ci sono più utenti da analizzare
        if(index >= this.family_tree.size()) return;
        
        // Recupera il prossimo user da analizzare con relativa etichetta
        User user = this.family_tree.get(index).getUser();
        String label = this.family_tree.get(index).getLabel();
        
        // Aggiungi la madre
        this.add(user.getMother(), label, "mother");
        // Aggiungi il padre
        this.add(user.getFather(), label, "father");
        
        // Aggiungi i figlie/figlie
        for(User child: user.getChildren()){
            if(child.getGender().equals("male")){
                // Aggiungi i figli
                this.add(child, label, "son");
            }else{
                // Aggiungi le figlie
                this.add(child, label, "daughter");
            }
        }
        
        // Aggiungi i fratelli/sorelle
        for(User sibling: user.getSiblings()){
            if(sibling.getGender().equals("male")){
                // Aggiungi i figli
                this.add(sibling, label, "brother");
            }else{
                // Aggiungi le figlie
                this.add(sibling, label, "sister");
            }
        }

        if(user.getGender().equals("male")){
            // Aggiungi la moglie
            this.add(user.getSpouse(), label, "wife");
        }else{
            // Aggiungi il marito
            this.add(user.getSpouse(), label, "husband");
        }
        
        // Valuta il prossimo utente
        createTree(++index);
        
    }
    private void add(User new_relative, String label, String degree){
        if(new_relative != null){
            // Se l'utente non è stato ancora valutato
            if(!this.family_tree.contains(new_relative)){
                String new_label = TreeNode.getNewLabel(label, degree);
                this.family_tree.add(new TreeNode(new_relative, new_label));
            }
        }
    }
    private void addAll(UserList list, String label, String degree){
        for (User element : list) {
            add(element, label, degree);
        }
    }
    
    /**
     * Restiruisci solo il nucleo familiare di un utente presente nell'albero genalogico
     * @param user
     * @return
     */
    public NodeList getFamily(User user) throws SQLException {
        
        NodeList family = new NodeList();
        
        // Recupero i membri del nucleo familiare
        UserList relatives = new UserList();
        relatives.addAll(user.getParents());
        relatives.addAll(user.getSiblings());
        relatives.addAll(user.getChildren());
        relatives.add(user.getSpouse());
        
        // Per ogni membro del nucleo familiare, recupero il nodo dell'albero corrispodente
        for(User relative: relatives){
            for(TreeNode node: this.family_tree){
                if(relative.getId().equals(node.getUser().getId())){
                    family.add(node);
                }
            }
        }
        // Restituisco il nucleo familiare con utenti etichettati
        return family;
        
        
        
    }   
    
    public TreeNode getUser(User user){
        for(TreeNode element: this.family_tree){
            if(element.getUser().equals(user)) return element;
        }
        return null;
    }
    
    public TreeNode getUserById(String id){
        for(TreeNode element: this.family_tree){
            if(element.getUser().getId().equals(id)) return element;
        }
        return null;
    }
    
    public NodeList getUsers(UserList users){
        NodeList relatives = new NodeList();
        for(User relative: users){
            for(TreeNode element: this.family_tree){
                if(element.getUser().equals(relative)) relatives.add(element);
            }
        }
        
        return relatives;
    }

}
