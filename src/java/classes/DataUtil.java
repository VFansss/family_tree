/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.EmailValidator;

/**
 *
 * @author Alex
 */
public class DataUtil {
    private static final String DATE_PATTERN =  "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
    
    /**
     * Controllo su String. Contiene solo caratteri alfanumerici?
     * NB: Di default gli spazi sono accettati.
     * @param toCheck stringa sul quale effettuare il controllo
     * @param space OPTIONAL - booleano. Se true accetta anche gli spazi.
     * @return true se la stringa soddisfa le condizioni date in input. false altrimenti.
     */
    public static boolean isAlphanmeric(String toCheck){
    
        return toCheck.matches("[a-zA-Z ]+");
        
    }
    
    public static boolean isAlphanumeric(String toCheck,boolean space){
    
        if (space){
            return toCheck.matches("[a-zA-Z ]+");
        }else{
            return toCheck.matches("[a-zA-Z]+");
        }
        
    }
    
    /**
     * Cancellazione doppi spazi interni alla stringa
     * @param toTrim Striga sul quale verrà  effettuato il trim.
     * @return Valore string - Stringa senza doppi spazi
     */
    public static String internalTrim(String toTrim){
        return toTrim.replaceAll("\\s+", " ");
    }
    
    /**
     * Controlla se la stringa ha una lunghezza definita 'anomala'.
     * @param toCheck Striga sul quale verrÃ  effettuato il controllo.
     * @param minval lunghezza minima (estremo compreso)
     * @param maxval lunghezza massima della stringa (estremo compreso)
     * @return false se all'interno dell'intervallo (estremi compresi). true altrimenti.
     */
    public static boolean checkLength(String toCheck,int minval,int maxval){   
        return toCheck.length()<minval || toCheck.length()>maxval;
    }
    
    /**
     * Trim degli spazi iniziali e dei doppi spazi interni
     */
    public static String spaceTrim(String toTrim){
        return DataUtil.internalTrim(toTrim.trim());
    }

    public static Message checkEmail(String email){
        
        String msg = null;
        boolean error = true;

        EmailValidator emailValidator=EmailValidator.getInstance();
        // Se l'email non è valida
        if(!(emailValidator.isValid(email))){
            msg = "Email is not valid"; 
            
        // Se l'utente è già registrato
        }else if(User.getUserByEmail(email) != null){
            msg = "User already exist";
        
        }else{
            error = false;
        }

        return new Message(msg, error);

    } 

    public static Message checkPassword(String toCheck){
    
        String msg = null;
        boolean error = true;
        
        // Se la password è lunga meno di sei caratteri
        if(toCheck.length() <6){
            msg = "La password deve contenere almeno 6 caratteri";
        
        // Se la password non è alfanumerica
        }else if(!DataUtil.isAlphanmeric(toCheck)){
            msg = "La password deve essere alfanumerica";
          
        }else{
            error = false;
        }

        return new Message(msg, error);
    }
    
    public static Message checkName(String toCheck){

        String msg = null;
        boolean error = true;

        //Check alphanumeric
        if(!DataUtil.isAlphanumeric(toCheck,true)){
            msg = "I dati anagrafici possono contenere solo caratteri alfanumerici (A-Z)";

        //Check lunghezza anomala
        //Nome 'anomalo': meno di 2 caratteri, piu di 50
        }else if(DataUtil.checkLength(toCheck,2,50)){
            //TODO: Esplicitare un messaggio diverso per i due casi?---------------------------------------------
            msg = "I dati anagrafici devono essere contenere almeno 2 caratteri ed essere piu corti di 50.";

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
            msg = "You can be only male or female";
            error = true;
        }
        
        return new Message(msg, error);
      
    }
    
    public static Message checkBirthplace(String toCheck){
    
        String msg = null;
        boolean error = false;

        //Check solo caratteri
        if(!(DataUtil.isAlphanumeric(toCheck, true))){
            msg = "La località di nascita può contenere solo caratteri alfanumerici!";
            error = true;
        }

        return new Message(msg, error);
    }
    
    public static Message checkBirthdate(String toCheck){
        
        String msg = null;
        boolean error = true;
        
        if(!DataUtil.validateDateFormat(toCheck)){
            msg = "La data non è nel formato giusto";
        }else{
            // Converti la data da String a Date
            Date date = DataUtil.stringToDate(toCheck, "dd/MM/yyyy");
            // Se la data non rientra nel range valido
            if(date == null || !DataUtil.validateDateRange(date)){
                msg = "La data di nascita non è valida";
            }else{
                error = false;
            }
        }
            
        return new Message(msg, error);
    }
    
    /*
        FUNZIONI MARCO
    */

    /**
     * Converto una data da String a Date
     * @param date      data da convertire
     * @param format    formato di date
     * @return          data nell formato yyyy-MM-dd
     */
    public static Date stringToDate(String date, String format){
        
        try {
            DateFormat formatter = new SimpleDateFormat(format);
            java.util.Date myDate;

            myDate = formatter.parse(date);
            Date sqlDate = new Date(myDate.getTime());
            return sqlDate;
        } catch (ParseException ex) {
            // Se il parse della data non va a buon fine, significa che la data non è nel formato giusto
            return null;
        }
    }
    
    /**
     * Controlla il formato della data (dd/MM/yyyy)
     * @param date  data da verificare
     * @return      true se la data è nel formato corretto, false altrimenti
     */
    public static boolean validateDateFormat(String date){
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches()){
            matcher.reset();
                if(matcher.find()){
                    String day = matcher.group(1);
                    String month = matcher.group(2);
                    if(month.length() == 1){
                        month = "0" + month;
                    }
                    int year = Integer.parseInt(matcher.group(3));
				 
                    if (day.equals("31") && 
                        (month.equals("04") || month .equals("06") || month.equals("09"))) {
			return false; // only 1,3,5,7,8,10,12 has 31 days
                    } else if (month.equals("02")) {
                        //leap year
                        if(year % 4==0){
                            return !(day.equals("30") || day.equals("31"));
                        }else{
                            return !(day.equals("29")||day.equals("30")||day.equals("31"));
                        }
                    }else{				 
                        return true;				 
                    }
                }else{
                    return false;
                }		  
            }else{
                return false;
            }
    }
    
    /**
     * Controlla se la data rientra nel range stabilito (01/01/1900 - oggi)
     * @param date  data da verificare
     * @return      true se la data è corretta, false altrimenti
     */
    public static boolean validateDateRange(Date date){
        // Calcola la data corrente
        java.util.Date current_date = new java.util.Date();
        // Calcola la data minima 01/01/1900)
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        java.util.Date min_date = cal.getTime();
        
        // Calcola i secondi della data da validare
        long date_second = date.getTime();
        // Calcola i secondi della data attuale
        long current_second = current_date.getTime();
        // Calcola i secondi della data minima
        long min_second = min_date.getTime();

        return !(date_second >= current_second || date_second <= min_second);
    }
    
    /**
     * Converte una data da Date a String
     * @param date  data da convertire
     * @return      stringa nel formato yyyy-MM-dd
     */
    public static String dateToString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
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
            StringBuffer sb = new StringBuffer();
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
    public static boolean crypt(String string_crypted, String to_check){
        
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
