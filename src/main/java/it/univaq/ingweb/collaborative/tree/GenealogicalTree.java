package it.univaq.ingweb.collaborative.tree;

import it.univaq.ingweb.collaborative.User;
import it.univaq.ingweb.collaborative.UserList;
import java.sql.SQLException;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Marco
 */

@ToString
@Getter
public class GenealogicalTree{
    // Contiene l'albero genealogico
    private final NodeList familyTree;

    public GenealogicalTree(User user){
        this.familyTree = new NodeList();
        this.familyTree.add(new TreeNode(user, "You")); 
    }
    
    /**
     * Restiruisci l'intero albero genealogico con utenti etichettati
     * @return
     * @throws java.sql.SQLException
     */
    public NodeList generateFamilyTree() throws SQLException {
        
        // Crea albero genealogico
        this.createTree(0);
        
        /* Successivamene non ci sarà bisogno di creare tutto l'albero già dall'inizo,
            ma si creerà inizialmente solo il nucleo familiare dell'utente, 
            che poi verrà ampliato in base al parente scelto
        */
        
        return this.familyTree;
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
        if(index >= this.familyTree.size()) return;
        
        // Recupera il prossimo user da analizzare con relativa etichetta
        User user = this.familyTree.get(index).getUser();
        String label = this.familyTree.get(index).getLabel();
        
        // Aggiungi la madre
        this.add(user.getRelative("mother"), label, "mother");
        // Aggiungi il padre
        this.add(user.getRelative("father"), label, "father");
        
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
            this.add(user.getRelative("wife"), label, "wife");
        }else{
            // Aggiungi il marito
            this.add(user.getRelative("husband"), label, "husband");
        }
        
        // Valuta il prossimo utente
        createTree(++index);
        
    }
    private void add(User new_relative, String label, String degree){
        if(new_relative != null){
            // Se l'utente non è stato ancora valutato
            if(!this.familyTree.contains(new_relative)){
                String new_label = TreeNode.getNewLabel(label, degree);
                this.familyTree.add(new TreeNode(new_relative, new_label));
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
     * @throws java.sql.SQLException
     */
    public NodeList getFamily(User user) throws SQLException {
        
        NodeList family = new NodeList();
        
        // Recupero i membri del nucleo familiare
        UserList relatives = new UserList();
        relatives.addAll(user.getParents());
        relatives.addAll(user.getSiblings());
        relatives.addAll(user.getChildren());
        relatives.add(user.getRelative("spouse"));
        
        // Per ogni membro del nucleo familiare, recupero il nodo dell'albero corrispodente
        for(User relative: relatives){
            for(TreeNode node: this.familyTree){
                if(relative.getId().equals(node.getUser().getId())){
                    family.add(node);
                }
            }
        }
        // Restituisco il nucleo familiare con utenti etichettati
        return family;
        
        
        
    }   
    
    public TreeNode getUser(User user){
        for(TreeNode element: this.familyTree){
            if(element.getUser().equals(user)) return element;
        }
        return null;
    }
    
    public TreeNode getUserById(String id){
        for(TreeNode element: this.familyTree){
            if(element.getUser().getId().equals(id)) return element;
        }
        return null;
    }
    
    public NodeList getUsers(UserList users){
        NodeList relatives = new NodeList();
        for(User relative: users){
            for(TreeNode element: this.familyTree){
                if(element.getUser().equals(relative)) relatives.add(element);
            }
        }
        if(relatives.isEmpty()) relatives = null;
        
        return relatives;
    }

}
