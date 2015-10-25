package Class.Tree;

import Class.User;
import java.util.Objects;


/**
 *
 * @author Marco
 */
public class TreeNode {
    private final String label;
    private final User user;
    
     public String getLabel() {
        return this.label;
    }

    public User getUser() {
        return this.user;
    }
    
    public TreeNode(User user, String label){
        this.label = label;
        this.user = user;
    }
    
    public static String getNewLabel(String label, String relationship){
        
        if(label.contains("You")) return relationship;
        
         // Se l'utente non ha una label, allora non l'avranno tutti gli utenti che aggiungerà all'albero
        if(label.equals("")) return "";

        // Se bisogna aggiungere un genitore di un nonno/a o un figlio di un nipote,
        if(((label.contains("great-grandmother") || label.contains("great-grandfather")) && (relationship.equals("mother") || relationship.equals("father")))
                || 
            (((label.contains("great-grandson") || label.contains("great-granddaughter")) && (relationship.equals("son") || relationship.equals("daughter"))))){
                        
            // Ritorna la label originale aggiungendo solamente un altro "great-"
            return label.replace("great-", "great-great-");          
        }  
        
        String new_label = "";
        boolean in_law = false;
        
        // Se è un parente acquisito
        if(label.contains("-in-law")){
            // Elimina la dicitura "-in-law"
            label = label.replace("-in-law", "");
            // Segnala che è stata eliminata la dicitura "-in-law" cosi da poterla riaggiungere in seguito
            in_law = true;
        }

        switch(relationship){
            
            case "father": 

                switch(label){

                    case "wife": 
                    case "husband":                         new_label = "father-in-law";                    break; 

                    case "father":                          new_label = "paternal grandfather";             break;
                    case "mother":                          new_label = "maternal grandfather";             break;

                    case "paternal grandfather":
                    case "paternal grandmother":            new_label = " paternal great-grandfather";      break;

                    case "maternal grandfather":
                    case "maternal grandmother":            new_label = " maternal great-grandfather";      break;
                }

                break;

            case "mother": 

                switch(label){

                    case "wife": 
                    case "husband":                         new_label = "mother-in-law";                    break; 

                    case "father":                          new_label = "paternal grandmother";             break;
                    case "mother":                          new_label = "maternal grandmother";             break;

                    case "paternal grandfather":
                    case "paternal grandmother":            new_label = " paternal great-grandmother";      break;

                    case "maternal grandfather":
                    case "maternal grandmother":            new_label = " maternal great-grandmother";      break;
                }

                break;

            case "wife": 

                switch(label){
                    case "son":                             new_label = "daughter-in-law";                  break;
                    case "brother":                         new_label = "sister-in-law";                    break;
                    case "paternal uncle":                  new_label = "paternal aunt-in-law";             break;
                    case "maternal uncle":                  new_label = "maternal aunt-in-law";             break;
                    
                    /*  
                        Se un padre deve inserire la popria moglie, 
                        allora suo figlio non ha inserito la moglie del padre
                        ciò vuol dire che quest'ultima non è la madre naturale del figlio
                    */
                    case "father":                          new_label = "stepmother";                       break; 
                    }

                break;

            case "husband": 

                switch(label){
                    case "daughter":                        new_label = "son-in-law";                       break;
                    case "sister":                          new_label = "brother-in-law";                   break;
                    case "maternal aunt":                   new_label = "maternal uncle-in-law";            break;
                    case "paternal aunt":                   new_label = "paternal uncle-in-law";            break;

                    /*  
                        Se una madre deve inserire il proprio marito, 
                        allora suo figlio non ha inserito il marito della madre
                        ciò vuol dire che quest'ultimo non è il padre naturale del figlio
                    */    
                    case "mother":                          new_label = "stepfather";                       break;
                }

                break;

            case "brother": 

                switch(label){
                    case "husband":                         
                    case "wife":                            new_label = "brother-in-law";                   break;

                    case "mother":                          new_label = "maternal uncle";                   break;
                    case "maternal grandfather":            new_label = "maternal great-uncle";             break;

                    case "father":                          new_label = "paternal uncle";                   break;
                    case "paternal grandfather":            new_label = "paternal great-uncle";             break;

                }

                break;

            case "sister": 

                switch(label){

                    case "husband":                         // cognato
                    case "wife":                            new_label = "sister-in-law";                    break;

                    case "mother":                          new_label = "maternal aunt";                    break;
                    case "maternal grandfather":            new_label = "maternal great-aunt";              break;

                    case "father":                          new_label = "paternal aunt";                    break;
                    case "paternal grandfather":            new_label = "paternal great-aunt";              break;

                }

                break;

            case "son": 

                switch(label){
                    /*  
                        Se una utente deve inserire il figlio del coniuge,
                        allora l'utente stesso non è stato inserito dal proprio figlio
                        quindi l'utente che sta inserendo è il suo figliastro
                    */ 
                    case "wife":
                    case "husband":                         new_label = "stepson";                          break;

                    case "sister":
                    case "brother":                         new_label = "nephew";                           break;

                    case "niece":                           
                    case "nephew":                          new_label = "grandnephew";                      break;

                    case "maternal aunt":
                    case "paternal aunt":
                    case "maternal uncle":
                    case "paternal uncle":                  new_label = "cousin";                           break;

                    case "stepmother":
                    case "stepfather":                      new_label = "stepbrother";                      break;

                    case "son":
                    case "daughter":                        new_label = "grandson";                         break;

                    case "granddaughter":   
                    case "grandson":                        new_label = "great-grandson";                   break;
                }

                break;

            case "daughter": 

                switch(label){
                    /*  
                        Se un'utente deve inserire la figlia del coniuge,
                        allora l'utente stesso non è stato inserito dalla propria figlia
                        quindi l'utente che sta inserendo è la sua figliastra
                    */ 
                    case "wife":
                    case "husband":                         new_label = "stepdaughter";                     break;
                    
                    case "sister":
                    case "brother":                         new_label = "niece";                            break;
                        
                    case "niece":                           
                    case "nephew":                          new_label = "grandniece";                       break;

                    case "maternal aunt":
                    case "paternal aunt":
                    case "maternal uncle":
                    case "paternal uncle":                  new_label = "cousin";                           break;

                    case "stepmother":
                    case "stepfather":                      new_label = "stepsister";                       break;

                    case "son":
                    case "daughter":                        new_label = "granddaughter";                    break;

                    case "granddaughter":   
                    case "grandson":                        new_label = "great-granddaughter";              break;
                }

                break;
        }

        // Se è un parente acquisito, aggiungi la dicitura "-in-law"
        if(in_law && !new_label.equals("") && !new_label.contains("-in-law")){
            new_label = new_label + "-in-law";
        }
           
        return new_label;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        final TreeNode other = (TreeNode) obj;
        
        return Objects.equals(this.user, other.user);
    }
   
}
