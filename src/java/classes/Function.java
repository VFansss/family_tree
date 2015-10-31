/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.io.PrintWriter;
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

/**
 *
 * @author Marco
 */
public class Function {
    private static final String DATE_PATTERN =  "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
    
    public static Date validateDate(String string_date){
        if(Function.validateDateFormat(string_date)){
            Date date = Function.stringToDate(string_date, "dd/MM/yyyy");
            
            if(date != null){
                if(Function.validateDateRange(date)){
                    return date;
                }
            }
        }
        return null;
    }
    
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
    
    public static String generateCode(int length){
        // Definisci caratteri ammessi
        char[] chars = "ABCDEFGHILMNOPQRTUVZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) sb.append(chars[random.nextInt(chars.length)]);
        return  sb.toString();
    }
}
