/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Marco
 */
public class Function {
    
//    public static String stringToDate(String birthdate){
//        // Conversione della data di nascita da String a Date
//        try {
//            DateFormat formatter = new SimpleDateFormat("");
//            java.util.Date myDate;
//            myDate = formatter.parse(birthdate);
//            Date sqlDate = new Date(myDate.getTime());
//            return sqlDate;
//        } catch (ParseException ex) {
//            // Se il parse della data non va a buon fine, significa che la data non è nel formato giusto
//            return null;
//        }
//    }
    
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

    public static String dateToString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    /**
     * Cripta una stringa
     * @param string    stringa da criptare
     * @return
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
     * Decripta una stringa
     * @param string_crypted    stringa da decriptare
     * @param to_check          stringa da verificare
     * @return
     */
    public static boolean crypt(String string_crypted, String to_check){
        
        return string_crypted.equals(crypt(to_check));
    }
}
