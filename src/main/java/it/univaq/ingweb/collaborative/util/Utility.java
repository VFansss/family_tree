package it.univaq.ingweb.collaborative.util;

import it.univaq.ingweb.collaborative.Database;
import it.univaq.ingweb.collaborative.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Random;
import org.apache.commons.validator.EmailValidator;

/**
 *
 * @author Alex
 */
public class Utility {
    
    /**
     * Controllo su String. Contiene solo caratteri alfanumerici?
     * @param toCheck   stringa sul quale effettuare il controllo
     * @param space     se true accetta anche gli spazi.
     * @return          true se la stringa è alfanumerica, false altrimenti.
     */
    public static boolean isAlphanumeric(String toCheck, boolean space){
        if(toCheck.equals("")) return true;
        
        if(space){
            return toCheck.matches("[a-zA-Z' ]+");
        }else{
            return toCheck.matches("[a-zA-Z']+");
        }
        
    }
    
    /**
     * Eliminazione degli spazi esterni e dei doppi spazi interni
     * @param toTrim    stringa da elaborare
     * @return          stringa "pulita"
     */
    public static String spaceTrim(String toTrim){
        return toTrim.trim().replaceAll("\\s+", " ");
    }

    public static Message checkEmail(String email){
        
        String msg = null;
        boolean error = true;

        // Se l'email non è valida
        if(!(EmailValidator.getInstance().isValid(email))){
            msg = "eml_3"; // Email is not valid
            
        // Se l'utente è già registrato
        }else{
            User user = User.getUserByEmail(email);
            if(user != null){
                try {
                    if(user.getPassword() != null){
                        msg = "usr_2"; // User already exist
                    }else{
                        Database.deleteRecord("user", "id = '" + user.getId() + "'");
                        Database.deleteRecord("request", "user_id = '" + user.getId() + "' OR relative_id = '" + user.getId() + "'");
       
                        error = false;
                    }
                } catch (SQLException ex) {
                    msg = "srv";
                }
            }else{
                error = false;
            }
        }
        return new Message(msg, error);

    } 

    public static Message checkPassword(String toCheck){
    
        String msg = null;
        boolean error = true;
        
        // Se la password è lunga meno di sei caratteri
        if(toCheck.length() <6){
            msg = "psd_3"; // The password must be 6 characters at least
        
        // Se la password non è alfanumerica
        }else if(!Utility.isAlphanumeric(toCheck, false)){
            msg = "psd_4"; // The password must be alphanumeric
          
        }else{
            error = false;
        }

        return new Message(msg, error);
    }
    
    public static Message checkName(String toCheck, String field){

        String msg = null;
        boolean error = true;

        //Check alphanumeric
        if(!Utility.isAlphanumeric(toCheck,true)){
            msg = field + "_1"; // The <field> must be alphanumeric

        //Check lunghezza anomala
        //Nome 'anomalo': meno di 2 caratteri, piu di 50
        }else if(toCheck.length() < 2 ){
            msg = field + "_2"; // The <field> is too short

        }else if(toCheck.length() > 50 ){
            msg = field + "_3"; // The <field> is too long
        }else{
            error = false;
        }
        
        return new Message(msg, error);

    }
        
    public static Message checkGender(String toCheck){
    
        String msg = null;
        boolean error = false;

        //Controllo su 'male' oppure 'female'
        if(!toCheck.equals("male") && !toCheck.equals("female") ){
            msg = "gnd"; // You can be only male or female
            error = true;
        }
        
        return new Message(msg, error);
      
    }
    
    public static Message checkBirthplace(String toCheck){
    
        String msg = null;
        boolean error = false;

        //Check solo caratteri
        if(!(Utility.isAlphanumeric(toCheck, true))){
            msg = "plc"; // The birthplace must be alphanumeric
            error = true;
        }

        return new Message(msg, error);
    }
    
    public static Message checkBirthdate(String toCheck){
        
        String msg = null;
        boolean error = true;
        
        if(!DateUtility.validateDateFormat(toCheck)){
            msg = "date_1"; // The date isn't in the right format
        }else{
            // Converti la data da String a Date
            Date date;
            try {
                date = DateUtility.stringToDate(toCheck);
                if(DateUtility.validateDateRange(date)){
                    error = false;
                }
            } catch (ParseException ex) {
                msg = "date_2"; // The date in not valid
            }
            // Se la data non rientra nel range valido
            
        }
            
        return new Message(msg, error);
    }
    
    
    public static Message checkData(String name, String surname, String gender, String birthdate, String birthplace){
       
            Message check = new Message("dt_ok", false);
            
            if(name.equals("") || surname.equals("") || gender == null || birthdate.equals("")  || birthplace.equals("")){
                check = new Message("fld", true); // All fields required

                // Se la data di nascita non è valida
                }else {

                    // Controllo del nome
                    check = Utility.checkName(name, "name");
                    if(!check.isError()) {

                        // Controllo del cognome
                        check = Utility.checkName(surname, "surname");
                        if(!check.isError()) {

                            // Controllo del sesso
                            check = Utility.checkGender(gender);
                            if(!check.isError()) {

                                // Controllo della città di nascita
                                check = Utility.checkBirthplace(birthplace);
                                if(!check.isError()) {

                                    // Controllo della data di nascita
                                    check = Utility.checkBirthdate(birthdate);
            }}}}}
            
            return check; 
            
    }
    /*
        FUNZIONI MARCO
    */

    
    /**
     * Cripta una stringa
     * @param string    stringa da criptare
     * @return          stringa criptata
     */
    public static String crypt(String string){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = string.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }

    }
    
    /**
     * Verifica se una stringa criptata è stata generata da un'altra stringa
     * @param string_crypted    stringa criptata
     * @param to_check          stringa da verificare
     * @return                  true se la password è stata verificata, false altrimenti
     */
    public static boolean decrypt(String string_crypted, String to_check){
        if(to_check == null || string_crypted == null) return false;
        return string_crypted.equals(crypt(to_check));
    }
    
    /**
     * Genera un codice alfanumerico
     * @param length    numero di caratteri del codice
     * @return          codice generato
     */
    public static String generateCode(int length){
        // Definisci caratteri ammessi
        char[] chars = "ABCDEFGHILMNOPQRTUVZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) sb.append(chars[random.nextInt(chars.length)]);
        return  sb.toString();
    }
    

}
