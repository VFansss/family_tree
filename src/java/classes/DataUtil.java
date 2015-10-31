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
    
    public boolean success;
    public String message;
    
    public DataUtil(){
        this.message="DataUtil_no_operation_done";
        this.success=false;
}
    
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
     * @param toTrim Striga sul quale verrÃ  effettuato il trim.
     * @return Valore string - Stringa senza doppi spazi
     */
    
    public static String internalTrim(String toTrim){
    
    return toTrim.replaceAll("\\s+", " ");
        
    }
    
    /**
     * 
     */
    
    public static String capitalizeCancaro(String stringa){

    return "";
        
    }
    
    
    /**
     * Controlla se la stringa ha una lunghezza definita 'anomala'.
     * @param toCheck Striga sul quale verrÃ  effettuato il controllo.
     * @param minval lunghezza minima (estremo compreso)
     * @param maxval lunghezza massima della stringa (estremo compreso)
     * @return false se all'interno dell'intervallo (estremi compresi). true altrimenti.
     */
    
    public static boolean anormalLength(String toCheck,int minval,int maxval){

    if(toCheck.length()<minval) return true;
    if(toCheck.length()>maxval) return true;
    
    return false;
    
    }
    
    /**
     * Trim degli spazi iniziali e dei doppi spazi interni
     */
    
    public static String spaceTrim(String toTrim){
    
    toTrim = toTrim.trim();
    toTrim = DataUtil.internalTrim(toTrim);
    
    return toTrim;
    
}
    
     /**
     * Effettua una serie di operazioni sul campo nome
     */
    
    public static DataUtil check_name(String toCheck){

    DataUtil reply = new DataUtil();
    
    toCheck=DataUtil.spaceTrim(toCheck);
    
    
    //Check alphanumeric
    if(!DataUtil.isAlphanumeric(toCheck)){
        reply.message="Il nome può contenere solo caratteri alfanumerici (A-Z)";
        reply.success=false;
        
        return reply;
    }
    
    //Check lunghezza anomala
    //Nome 'anomalo': meno di 2 caratteri, piu di 50
    if(DataUtil.anormalLength(toCheck,2,50)){
        //TODO: Esplicitare un messaggio diverso per i due casi?---------------------------------------------
        reply.message="Il nome deve essere contenere almeno 2 caratteri ed essere piu corto di 50.";
        reply.success=false;
        return reply;
    }
    
    
    //Se e' arrivato a questo puntp ha superato tutti i controlli
    //E' un nome considerabile 'OK'
    reply.message="Il nome e' valido!";
    reply.success=true;
    return reply;
    }
    
    
//END OF CLASS    
}
