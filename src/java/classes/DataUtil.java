/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Alex
 */
public class DataUtil {
    
    /**
     * Controllo su String. Contiene solo caratteri alfanumerici?
     * @param toCheck stringa sul quale effettuare il controllo
     * @return true se la stringa contiene solo caratteri alfanumerici. false altrimenti.
     */
    
    public static boolean isAlphanumeric(String toCheck){
    
        return toCheck.matches("[a-zA-Z]+");
        
    }
    
    /**
     * Cancellazione doppi spazi interni alla stringa
     * @param toTrim Striga sul quale verrà effettuato il trim.
     * @return Valore string - Stringa senza doppi spazi
     */
    
    public static String internalTrim(String toTrim){
    
    return toTrim.replaceAll("\\s+", " ");
        
    }
    
    /**
     * Capitalizza prima lettera di ogni parola nella stringa
     * @param toTrim Striga sul quale verrà effettuato il trim.
     * @return Valore string - Stringa senza doppi spazi
     */
    
    public static String capitalizeEachWord(String toCapitalize){

    //TODO
        
    return toCapitalize;
    
    }
    
    
    /**
     * Controlla se la stringa ha una lunghezza definita 'anomala'.
     * @param toCheck Striga sul quale verrà effettuato il controllo.
     * @param minval lunghezza minima (estremo compreso)
     * @param maxval lunghezza massima della stringa (estremo compreso)
     * @return false se all'interno dell'intervallo (estremi compresi). true altrimenti.
     */
    
    public static boolean anormalLength(String toCheck,int minval,int maxval){

    if(toCheck.length()<minval) return true;
    if(toCheck.length()>maxval) return true;
    
    return false;
    
    }
    
    
    
}
