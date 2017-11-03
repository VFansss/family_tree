package it.univaq.ingweb.collaborative.tree;

import java.util.Objects;

import it.univaq.ingweb.collaborative.User;


/**
 *
 * @author Marco
 */

public final class TreeNode {
    private final String label;
    private final User user;
    
    
    public String getLabel() {
		return label;
	}

	public User getUser() {
		return user;
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
        
        String newLabel = "";
        boolean inLaw = false;
        
        // Se è un parente acquisito
        if(label.contains("-in-law")){
            // Elimina la dicitura "-in-law"
            label = label.replace("-in-law", "");
            // Segnala che è stata eliminata la dicitura "-in-law" cosi da poterla riaggiungere in seguito
            inLaw = true;
        }

        switch(relationship){
            
            case "father": 

                switch(label){

                    case "wife": 
                    case "husband":                         newLabel = "father-in-law";                    break; 

                    case "father":                          newLabel = "paternal grandfather";             break;
                    case "mother":                          newLabel = "maternal grandfather";             break;

                    case "paternal grandfather":
                    case "paternal grandmother":            newLabel = " paternal great-grandfather";      break;

                    case "maternal grandfather":
                    case "maternal grandmother":            newLabel = " maternal great-grandfather";      break;
                }

                break;

            case "mother": 

                switch(label){

                    case "wife": 
                    case "husband":                         newLabel = "mother-in-law";                    break; 

                    case "father":                          newLabel = "paternal grandmother";             break;
                    case "mother":                          newLabel = "maternal grandmother";             break;

                    case "paternal grandfather":
                    case "paternal grandmother":            newLabel = " paternal great-grandmother";      break;

                    case "maternal grandfather":
                    case "maternal grandmother":            newLabel = " maternal great-grandmother";      break;
                }

                break;

            case "wife": 

                switch(label){
                    case "son":                             newLabel = "daughter-in-law";                  break;
                    case "brother":                         newLabel = "sister-in-law";                    break;
                    case "paternal uncle":                  newLabel = "paternal aunt-in-law";             break;
                    case "maternal uncle":                  newLabel = "maternal aunt-in-law";             break;
                    
                    /*  
                        Se un padre deve inserire la popria moglie, 
                        allora suo figlio non ha inserito la moglie del padre
                        ciò vuol dire che quest'ultima non è la madre naturale del figlio
                    */
                    case "father":                          newLabel = "stepmother";                       break; 
                    }

                break;

            case "husband": 

                switch(label){
                    case "daughter":                        newLabel = "son-in-law";                       break;
                    case "sister":                          newLabel = "brother-in-law";                   break;
                    case "maternal aunt":                   newLabel = "maternal uncle-in-law";            break;
                    case "paternal aunt":                   newLabel = "paternal uncle-in-law";            break;

                    /*  
                        Se una madre deve inserire il proprio marito, 
                        allora suo figlio non ha inserito il marito della madre
                        ciò vuol dire che quest'ultimo non è il padre naturale del figlio
                    */    
                    case "mother":                          newLabel = "stepfather";                       break;
                }

                break;

            case "brother": 

                switch(label){
                    case "husband":                         
                    case "wife":                            newLabel = "brother-in-law";                   break;

                    case "mother":                          newLabel = "maternal uncle";                   break;
                    case "maternal grandfather":            newLabel = "maternal great-uncle";             break;

                    case "father":                          newLabel = "paternal uncle";                   break;
                    case "paternal grandfather":            newLabel = "paternal great-uncle";             break;

                }

                break;

            case "sister": 

                switch(label){

                    case "husband":                         // cognato
                    case "wife":                            newLabel = "sister-in-law";                    break;

                    case "mother":                          newLabel = "maternal aunt";                    break;
                    case "maternal grandfather":            newLabel = "maternal great-aunt";              break;

                    case "father":                          newLabel = "paternal aunt";                    break;
                    case "paternal grandfather":            newLabel = "paternal great-aunt";              break;

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
                    case "husband":                         newLabel = "stepson";                          break;

                    case "sister":
                    case "brother":                         newLabel = "nephew";                           break;

                    case "niece":                           
                    case "nephew":                          newLabel = "grandnephew";                      break;

                    case "maternal aunt":
                    case "paternal aunt":
                    case "maternal uncle":
                    case "paternal uncle":                  newLabel = "cousin";                           break;
                    
                    case "mother":
                    case "father": 
                    case "stepmother":
                    case "stepfather":                      newLabel = "stepbrother";                      break;

                    case "son":
                    case "daughter":                        newLabel = "grandson";                         break;

                    case "granddaughter":   
                    case "grandson":                        newLabel = "great-grandson";                   break;
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
                    case "husband":                         newLabel = "stepdaughter";                     break;
                    
                    case "sister":
                    case "brother":                         newLabel = "niece";                            break;
                        
                    case "niece":                           
                    case "nephew":                          newLabel = "grandniece";                       break;

                    case "maternal aunt":
                    case "paternal aunt":
                    case "maternal uncle":
                    case "paternal uncle":                  newLabel = "cousin";                           break;
                    
                    case "mother":
                    case "father": 
                    case "stepmother":
                    case "stepfather":                      newLabel = "stepsister";                       break;

                    case "son":
                    case "daughter":                        newLabel = "granddaughter";                    break;

                    case "granddaughter":   
                    case "grandson":                        newLabel = "great-granddaughter";              break;
                }

                break;
        }

        // Se è un parente acquisito, aggiungi la dicitura "-in-law"
        if(inLaw && !newLabel.equals("") && !newLabel.contains("-in-law")){
            newLabel = newLabel + "-in-law";
        }
           
        return newLabel;
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
